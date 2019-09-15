package pers.acp.admin.oauth.bus.publish

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        val source = this
        GlobalScope.launch(Dispatchers.IO) {
            applicationContext.publishEvent(RefreshApplicationEvent(busProperties.id, null, "refresh client", source))
        }
    }

    fun doNotifyUpdateRuntime() {
        val source = this
        GlobalScope.launch(Dispatchers.IO) {
            applicationContext.publishEvent(RefreshRuntimeEvent(busProperties.id, null, "refresh runtime", source))
        }
    }

}
