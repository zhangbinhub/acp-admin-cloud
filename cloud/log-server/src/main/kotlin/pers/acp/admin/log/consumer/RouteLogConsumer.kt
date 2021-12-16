package pers.acp.admin.log.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import pers.acp.admin.log.conf.LogServerCustomerConfiguration
import pers.acp.admin.log.domain.LogDomain
import pers.acp.admin.log.message.RouteLogMessage
import io.github.zhangbinhub.acp.core.task.BaseAsyncTask
import io.github.zhangbinhub.acp.core.task.threadpool.ThreadPoolService
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import java.util.function.Consumer

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class RouteLogConsumer(
    private val logAdapter: LogAdapter,
    private val objectMapper: ObjectMapper,
    private val logDomain: LogDomain,
    private val logServerCustomerConfiguration: LogServerCustomerConfiguration
) : Consumer<String> {
    override fun accept(message: String) {
        logAdapter.debug("收到 kafka 消息：$message")
        ThreadPoolService.getInstance(1, 1, Int.MAX_VALUE)
            .addTask(object : BaseAsyncTask("route_log", false) {
                override fun beforeExecuteFun(): Boolean = true
                override fun executeFun(): Any {
                    try {
                        objectMapper.readValue(message, RouteLogMessage::class.java)?.also {
                            runBlocking {
                                if (logServerCustomerConfiguration.routeLogEnabled) {
                                    launch(Dispatchers.IO) {
                                        logDomain.doRouteLog(it, message)
                                    }
                                }
                                it.token?.apply {
                                    if (it.responseStatus != null) {
                                        if (logServerCustomerConfiguration.operateLogEnabled) {
                                            launch(Dispatchers.IO) {
                                                logDomain.doOperateLog(it, message)
                                            }
                                        }
                                        if (it.responseStatus == HttpStatus.OK.value()) {
                                            if (it.applyToken) {
                                                launch(Dispatchers.IO) {
                                                    logDomain.doLoginLog(it, message)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        logAdapter.error("日志消息：$message \n处理失败：${e.message}", e)
                    }
                    return true
                }

                override fun afterExecuteFun(result: Any) {}
            })
    }

}
