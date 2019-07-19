package pers.acp.admin.oauth.bus.listener

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import pers.acp.admin.oauth.bus.event.RefreshApplicationEvent
import pers.acp.admin.oauth.domain.security.SecurityClientDetailsDomain
import pers.acp.spring.cloud.log.LogInstance

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
@Component
class RefreshApplicationEventListener @Autowired
constructor(private val logInstance: LogInstance,
            private val objectMapper: ObjectMapper,
            private val securityClientDetailsDomain: SecurityClientDetailsDomain) : ApplicationListener<RefreshApplicationEvent> {

    override fun onApplicationEvent(refreshApplicationEvent: RefreshApplicationEvent) {
        logInstance.info("收到更新应用信息事件：" + refreshApplicationEvent.message!!)
        try {
            logInstance.debug(objectMapper.writeValueAsString(refreshApplicationEvent))
            logInstance.info("开始刷新client数据...")
            securityClientDetailsDomain.loadClientInfo()
            logInstance.info("client数据刷新完成！")
        } catch (e: Exception) {
            logInstance.error(e.message, e)
        }

    }

}
