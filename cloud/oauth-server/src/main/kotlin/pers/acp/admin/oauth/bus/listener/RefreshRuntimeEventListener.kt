package pers.acp.admin.oauth.bus.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import pers.acp.admin.common.event.ReloadDataBusEvent
import pers.acp.admin.constant.BusEventMessage
import pers.acp.admin.oauth.domain.RuntimeConfigDomain
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
@Component
class RefreshRuntimeEventListener @Autowired
constructor(private val logAdapter: LogAdapter,
            private val objectMapper: ObjectMapper,
            private val runtimeConfigDomain: RuntimeConfigDomain) : ApplicationListener<ReloadDataBusEvent> {

    override fun onApplicationEvent(reloadDataBusEvent: ReloadDataBusEvent) {
        if (reloadDataBusEvent.message == BusEventMessage.refreshRuntime) {
            logAdapter.info("收到更新运行参数数据事件：" + reloadDataBusEvent.message)
            try {
                logAdapter.debug(objectMapper.writeValueAsString(reloadDataBusEvent))
                logAdapter.info("开始刷新运行参数数据...")
                runtimeConfigDomain.loadRuntimeConfig()
                logAdapter.info("运行参数数据刷新完成！")
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
            }
        }
    }
}
