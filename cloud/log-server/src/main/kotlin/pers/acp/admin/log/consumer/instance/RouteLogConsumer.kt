package pers.acp.admin.log.consumer.instance

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.StreamListener
import pers.acp.admin.log.conf.LogServerCustomerConfiguration
import pers.acp.admin.log.constant.LogServerConstant
import pers.acp.admin.log.domain.LogDomain
import pers.acp.admin.log.message.RouteLogMessage

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class RouteLogConsumer
constructor(private val objectMapper: ObjectMapper,
            private val logDomain: LogDomain,
            private val logServerCustomerConfiguration: LogServerCustomerConfiguration) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @StreamListener(LogServerConstant.ROUTE_LOG_INPUT)
    fun process(message: String) {
        log.debug("收到 kafka 消息：$message")
        try {
            objectMapper.readValue(message, RouteLogMessage::class.java)?.also {
                it.token?.apply {
                    if (logServerCustomerConfiguration.routeLog) {
                        logDomain.doRouteLog(it)
                    }
                    if (logServerCustomerConfiguration.operateLog) {
                        logDomain.doOperateLog(it)
                    }
                }
            }
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

}
