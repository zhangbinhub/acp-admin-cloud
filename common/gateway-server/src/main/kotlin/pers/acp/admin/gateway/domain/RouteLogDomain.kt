package pers.acp.admin.gateway.domain

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange
import pers.acp.admin.gateway.constant.GateWayConstant
import pers.acp.admin.gateway.message.RouteLogMessage
import pers.acp.admin.gateway.producer.RouteLogOutput
import pers.acp.admin.gateway.producer.instance.RouteLogProducer
import java.lang.StringBuilder

import java.net.URI
import java.nio.charset.Charset
import java.util.LinkedHashSet

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class RouteLogDomain @Autowired
constructor(private val environment: Environment,
            private val routeLogOutput: RouteLogOutput,
            private val objectMapper: ObjectMapper) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun routeLogProducer() = RouteLogProducer(routeLogOutput, objectMapper)

    private fun getRealRemoteIp(request: ServerHttpRequest): String {
        var ipAddress: String? = request.headers.getFirst("X-Forwarded-For")
        if (ipAddress == null || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.headers.getFirst("Citrix-Client-IP")
        }
        if (ipAddress == null || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.headers.getFirst("Proxy-Client-IP")
        }
        if (ipAddress == null || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.headers.getFirst("WL-Proxy-Client-IP")
        }
        if (ipAddress == null || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.remoteAddress!!.hostString
        }
        if (ipAddress != null && ipAddress.length > 15) {
            if (ipAddress.contains(",")) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","))
            }
        } else if (ipAddress == null) {
            ipAddress = ""
        }
        return ipAddress
    }

    private fun doLog(routeLogMessage: RouteLogMessage) {
        try {
            routeLogProducer().doNotifyRouteLog(routeLogMessage)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    fun beforeRoute(serverWebExchange: ServerWebExchange) {
        doLog(createRouteLogMessage(serverWebExchange))
    }

    fun afterRoute(serverWebExchange: ServerWebExchange, dataBufferList: MutableList<out DataBuffer?>? = null): ByteArray {
        try {
            val routeLogMessage = createRouteLogMessage(serverWebExchange)
            val responseTime = System.currentTimeMillis()
            routeLogMessage.processTime = responseTime - routeLogMessage.requestTime!!
            routeLogMessage.responseTime = responseTime
            serverWebExchange.response.statusCode?.let {
                routeLogMessage.responseStatus = it.value()
            }
            if (routeLogMessage.targetPath == GateWayConstant.TARGET_APPLY_TOKEN_PATH) {
                routeLogMessage.applyToken = true
            }
            var content: ByteArray = byteArrayOf()
            dataBufferList?.let {
                if ((serverWebExchange.response.headers.getFirst(HttpHeaders.CONTENT_TYPE) ?: "")
                                .contains(MediaType.APPLICATION_JSON_VALUE, true)) {
                    try {
                        val responseContent = StringBuilder()
                        dataBufferList.forEach { dataBuffer ->
                            dataBuffer?.let {
                                responseContent.append(it.asInputStream(true).readAllBytes().toString(Charset.forName("UTF-8")))
                            }
                        }
                        val responseString = responseContent.toString()
                        content = responseString.toByteArray(Charset.forName("UTF-8"))
                        objectMapper.readTree(responseString)?.also { json ->
                            if (json.has(GateWayConstant.GATEWAY_HEADER_RESPONSE_TOKEN_FIELD)) {
                                json.path(GateWayConstant.GATEWAY_HEADER_RESPONSE_TOKEN_FIELD)?.apply {
                                    routeLogMessage.token = this.textValue()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        log.error(e.message, e)
                    }
                }
            }
            doLog(routeLogMessage)
            return content
        } catch (e: Exception) {
            log.error(e.message, e)
            return byteArrayOf()
        }
    }

    /**
     * 构建路由日志消息
     */
    private fun createRouteLogMessage(serverWebExchange: ServerWebExchange): RouteLogMessage {
        var token: String? = null
        val authorization = serverWebExchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        authorization?.apply {
            val authList = this.split(" ")
            if (authList.size > 1 && (authList[0].equals("Bearer", ignoreCase = true) || authList[0].equals("mac", ignoreCase = true))) {
                token = authList[1]
            }
        }
        val routeLogMessage = RouteLogMessage(
                logId = serverWebExchange.logPrefix.replace(Regex("[\\[|\\]]"), ""),
                remoteIp = getRealRemoteIp(serverWebExchange.request),
                gatewayIp = environment.getProperty("server.address"),
                method = serverWebExchange.request.methodValue,
                token = token
        )
        val uris = serverWebExchange.getAttribute<LinkedHashSet<URI>>(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR)
        if (uris != null) {
            for (uri in uris) {
                val prefix = "lb://"
                val host = routeLogMessage.gatewayIp!! + ":" + environment.getProperty("server.port")!!
                if (routeLogMessage.path == null && uri.toString().contains(host)) {
                    routeLogMessage.path = uri.toString().substring(uri.toString().indexOf(host) + host.length)
                } else if (routeLogMessage.serverId == null && uri.toString().startsWith(prefix)) {
                    val s1 = uri.toString().substring(prefix.length)
                    if (s1.contains("/")) {
                        routeLogMessage.serverId = s1.substring(0, s1.indexOf("/"))
                    } else {
                        routeLogMessage.serverId = s1
                    }
                }
            }
        }
        val uri = serverWebExchange.getAttribute<URI>(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)
        uri?.let {
            routeLogMessage.targetIp = it.host
            routeLogMessage.targetUri = it.toString()
            routeLogMessage.targetPath = it.path
        }
        routeLogMessage.requestTime = System.currentTimeMillis()
        serverWebExchange.request.headers.getFirst(GateWayConstant.GATEWAY_HEADER_REQUEST_TIME)?.let {
            routeLogMessage.requestTime = it.toLong()
        }
        return routeLogMessage
    }

}
