package pers.acp.admin.oauth.bus.publish

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.bus.BusProperties
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import pers.acp.admin.oauth.bus.event.RefreshApplicationEvent
import pers.acp.admin.oauth.bus.event.RefreshRuntimeEvent

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
@Component
class RefreshEventPublish @Autowired
constructor(private val applicationContext: ApplicationContext, private val busProperties: BusProperties) {

    fun doNotifyUpdateApp() {
        applicationContext.publishEvent(RefreshApplicationEvent(this, busProperties.id, null, "refresh client"))
    }

    fun doNotifyUpdateRuntime() {
        applicationContext.publishEvent(RefreshRuntimeEvent(this, busProperties.id, null, "refresh runtime"))
    }

}
