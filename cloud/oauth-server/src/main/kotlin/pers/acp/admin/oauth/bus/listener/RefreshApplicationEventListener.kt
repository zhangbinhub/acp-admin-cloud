package pers.acp.admin.oauth.bus.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import pers.acp.admin.common.event.ReloadDataBusEvent
import pers.acp.admin.oauth.constant.BusEventMessage
import pers.acp.admin.oauth.domain.security.SecurityClientDetailsDomain
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
@Component
class RefreshApplicationEventListener @Autowired
constructor(private val logAdapter: LogAdapter,
            private val objectMapper: ObjectMapper,
            private val securityClientDetailsDomain: SecurityClientDetailsDomain) : ApplicationListener<ReloadDataBusEvent> {

    override fun onApplicationEvent(reloadDataBusEvent: ReloadDataBusEvent) {
        if (reloadDataBusEvent.message == BusEventMessage.refreshApplication) {
            logAdapter.info("收到更新应用信息事件：" + reloadDataBusEvent.message)
            try {
                logAdapter.debug(objectMapper.writeValueAsString(reloadDataBusEvent))
                Thread(Runnable {
                    logAdapter.info("开始刷新client数据...")
                    securityClientDetailsDomain.loadClientInfo()
                    logAdapter.info("client数据刷新完成！")
                }).apply {
                    this.isDaemon = true
                }.start()
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
            }
        }
    }
}
