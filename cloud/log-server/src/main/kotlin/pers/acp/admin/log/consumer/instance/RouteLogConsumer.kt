package pers.acp.admin.log.consumer.instance

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.http.HttpStatus
import pers.acp.admin.constant.RouteConstant
import pers.acp.admin.log.conf.LogServerCustomerConfiguration
import pers.acp.admin.log.domain.LogDomain
import pers.acp.admin.log.message.RouteLogMessage
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class RouteLogConsumer
constructor(private val logAdapter: LogAdapter,
            private val objectMapper: ObjectMapper,
            private val logDomain: LogDomain,
            private val logServerCustomerConfiguration: LogServerCustomerConfiguration) {

    @StreamListener(RouteConstant.ROUTE_LOG_INPUT)
    fun process(message: String) {
        logAdapter.debug("收到 kafka 消息：$message")
        try {
            objectMapper.readValue(message, RouteLogMessage::class.java)?.also {
                if (logServerCustomerConfiguration.routeLogEnabled) {
                    GlobalScope.launch(Dispatchers.IO) {
                        logDomain.doRouteLog(it, message)
                    }
                }
                it.token?.apply {
                    if (it.responseStatus == HttpStatus.OK.value()) {
                        if (logServerCustomerConfiguration.operateLogEnabled) {
                            GlobalScope.launch(Dispatchers.IO) {
                                logDomain.doOperateLog(it, message)
                            }
                        }
                        if (it.applyToken) {
                            GlobalScope.launch(Dispatchers.IO) {
                                logDomain.doLoginLog(it, message)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logAdapter.error("日志消息：$message \n处理失败：${e.message}", e)
        }

    }

}
