package pers.acp.admin.deploy.bus.publish

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.bus.BusProperties
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import pers.acp.admin.common.event.ExecuteBusEvent
import pers.acp.admin.deploy.constant.DeployConstant

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
@Component
class DeployEventPublish @Autowired
constructor(private val applicationContext: ApplicationContext,
            private val busProperties: BusProperties,
            private val objectMapper: ObjectMapper) {
    fun doNotifyExecuteDeploy(deployTaskId: String) {
        val source = this
        GlobalScope.launch(Dispatchers.IO) {
            applicationContext.publishEvent(ExecuteBusEvent(busProperties.id, null, DeployConstant.busMessageExecuteDeploy,
                    listOf(deployTaskId), source))
        }
    }
}
