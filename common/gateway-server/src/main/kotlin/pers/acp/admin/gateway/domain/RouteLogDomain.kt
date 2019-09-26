package pers.acp.admin.gateway.domain

import com.fasterxml.jackson.databind.ObjectMapper
import pers.acp.admin.gateway.constant.GateWayConstant
import pers.acp.admin.gateway.message.RouteLogMessage
import pers.acp.admin.gateway.producer.RouteLogOutput
import pers.acp.admin.gateway.producer.instance.RouteLogProducer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange

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

    fun beforeRoute(routeLogMessage: RouteLogMessage) {
        doLog(routeLogMessage)
    }

    fun afterRoute(routeLogMessage: RouteLogMessage, dataBufferList: MutableList<out DataBuffer?>? = null): ByteArray {
        try {
            var content: ByteArray = byteArrayOf()
            dataBufferList?.let { list ->
                try {
                    list.forEach { dataBuffer ->
                        dataBuffer?.let {
                            content = content.plus(it.asInputStream(true).readAllBytes())
                        }
                    }
                    val responseString = content.toString(Charset.forName("UTF-8"))
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
    fun createRouteLogMessage(serverWebExchange: ServerWebExchange): RouteLogMessage {
        var token: String? = null
        serverWebExchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)?.apply {
            val authList = this.split(" ")
            if (authList.size > 1 && (authList[0].equals("Bearer", ignoreCase = true) || authList[0].equals("mac", ignoreCase = true))) {
                token = authList[1]
            }
        }
        return RouteLogMessage(
                logId = serverWebExchange.logPrefix.replace(Regex("[\\[|\\]]"), "").trim(),
                remoteIp = getRealRemoteIp(serverWebExchange.request),
                gatewayIp = (environment.getProperty("server.address")
                        ?: "") + ":" + environment.getProperty("server.port"),
                method = serverWebExchange.request.methodValue,
                token = token
        ).also { routeLogMessage ->
            serverWebExchange.getAttribute<LinkedHashSet<URI>>(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR)?.forEach {
                val prefix = "lb://"
                val uriStr = it.toString()
                if (routeLogMessage.path == null && !uriStr.startsWith(prefix)) {
                    var tmpUri = uriStr
                    if (tmpUri.startsWith("http://")) {
                        tmpUri = tmpUri.substring(7)
                    } else if (tmpUri.startsWith("https://")) {
                        tmpUri = tmpUri.substring(8)
                    }
                    if (tmpUri.contains("/")) {
                        routeLogMessage.path = tmpUri.substring(tmpUri.indexOf("/"))
                    } else {
                        routeLogMessage.path = tmpUri
                    }
                }
                if (routeLogMessage.serverId == null && uriStr.startsWith(prefix)) {
                    val tmpUri = uriStr.substring(prefix.length)
                    if (tmpUri.contains("/")) {
                        routeLogMessage.serverId = tmpUri.substring(0, tmpUri.indexOf("/"))
                    } else {
                        routeLogMessage.serverId = tmpUri
                    }
                }
            }
            serverWebExchange.getAttribute<URI>(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)?.apply {
                routeLogMessage.targetIp = this.host
                routeLogMessage.targetUri = this.toString()
                routeLogMessage.targetPath = this.path
                if (routeLogMessage.targetPath == GateWayConstant.TARGET_APPLY_TOKEN_PATH) {
                    routeLogMessage.applyToken = true
                }
            }
            routeLogMessage.requestTime = System.currentTimeMillis()
            serverWebExchange.request.headers.getFirst(GateWayConstant.GATEWAY_HEADER_REQUEST_TIME)?.apply {
                routeLogMessage.requestTime = this.toLong()
            }
        }
    }

}
