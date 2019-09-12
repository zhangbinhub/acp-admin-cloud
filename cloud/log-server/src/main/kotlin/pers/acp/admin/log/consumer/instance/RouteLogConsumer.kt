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
                if (logServerCustomerConfiguration.routeLogEnabled) {
                    logDomain.doRouteLog(it)
                }
                it.token?.apply {
                    if (it.responseStatus == 200) {
                        if (logServerCustomerConfiguration.operateLogEnabled) {
                            logDomain.doOperateLog(it)
                        }
                        if (it.applyToken) {
                            logDomain.doLoginLog(it)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            log.error("日志消息：$message \n处理失败：${e.message}", e)
        }

    }

}
