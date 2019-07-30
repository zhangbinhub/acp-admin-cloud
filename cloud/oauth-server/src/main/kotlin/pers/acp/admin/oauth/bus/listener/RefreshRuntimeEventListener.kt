package pers.acp.admin.oauth.bus.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import pers.acp.admin.oauth.bus.event.RefreshRuntimeEvent
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
            private val runtimeConfigDomain: RuntimeConfigDomain) : ApplicationListener<RefreshRuntimeEvent> {

    override fun onApplicationEvent(refreshRuntimeEvent: RefreshRuntimeEvent) {
        logAdapter.info("收到更新应用信息事件：" + refreshRuntimeEvent.message!!)
        try {
            logAdapter.debug(objectMapper.writeValueAsString(refreshRuntimeEvent))
            logAdapter.info("开始刷新运行参数数据...")
            runtimeConfigDomain.loadRuntimeConfig()
            logAdapter.info("运行参数数据刷新完成！")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }

    }
}
