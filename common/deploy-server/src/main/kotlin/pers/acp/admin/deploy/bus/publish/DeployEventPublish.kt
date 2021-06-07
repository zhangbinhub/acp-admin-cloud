package pers.acp.admin.deploy.bus.publish

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
constructor(
    private val applicationContext: ApplicationContext,
    private val busProperties: BusProperties
) {
    fun doNotifyExecuteDeploy(deployTaskId: String) {
        applicationContext.publishEvent(
            ExecuteBusEvent(
                busProperties.id, null, DeployConstant.busMessageExecuteDeploy,
                listOf(deployTaskId), this
            )
        )
    }
}
