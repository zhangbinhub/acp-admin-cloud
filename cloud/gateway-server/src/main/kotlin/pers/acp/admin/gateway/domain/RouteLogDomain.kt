package pers.acp.admin.gateway.domain

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange
import pers.acp.admin.gateway.po.RouteLogPO
import pers.acp.admin.gateway.producer.RouteLogOutput
import pers.acp.admin.gateway.producer.instance.RouteLogProducer

import java.net.URI
import java.util.LinkedHashSet

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class RouteLogDomain @Autowired
constructor(private val environment: Environment, private val routeLogOutput: RouteLogOutput, private val objectMapper: ObjectMapper) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun routeLogProducer() = RouteLogProducer(routeLogOutput, objectMapper)

    @Transactional
    fun beforeRoute(serverWebExchange: ServerWebExchange): RouteLogPO {
        val routeLogPO = RouteLogPO(
                remoteIp = if (serverWebExchange.request.remoteAddress != null) serverWebExchange.request.remoteAddress!!.hostString else "",
                gatewayIp = environment.getProperty("server.address")
        )
        val uris = serverWebExchange.getAttribute<LinkedHashSet<URI>>(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR)
        if (uris != null) {
            for (uri in uris) {
                val prefix = "lb://"
                val host = routeLogPO.gatewayIp!! + ":" + environment.getProperty("server.port")!!
                if (routeLogPO.path == null && uri.toString().contains(host)) {
                    routeLogPO.path = uri.toString().substring(uri.toString().indexOf(host) + host.length)
                } else if (routeLogPO.serverId == null && uri.toString().startsWith(prefix)) {
                    val s1 = uri.toString().substring(prefix.length)
                    if (s1.contains("/")) {
                        routeLogPO.serverId = s1.substring(0, s1.indexOf("/"))
                    } else {
                        routeLogPO.serverId = s1
                    }
                }
            }
        }
        val uri = serverWebExchange.getAttribute<URI>(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)
        uri?.let {
            routeLogPO.targetUri = it.toString()
        }
        routeLogPO.requestTime = System.currentTimeMillis()
        return routeLogPO
    }

    @Transactional
    fun afterRoute(serverWebExchange: ServerWebExchange, routeLogPO: RouteLogPO) {
        try {
            val responseTime = System.currentTimeMillis()
            routeLogPO.processTime = responseTime - routeLogPO.requestTime!!
            routeLogPO.responseTime = responseTime
            serverWebExchange.response.statusCode?.let {
                routeLogPO.responseStatus = it.value()
            }
            routeLogProducer().doNotifyRouteLog(routeLogPO)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

}
