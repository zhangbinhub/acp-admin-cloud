package pers.acp.admin.deploy.conf

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component
import pers.acp.spring.cloud.component.CloudTools

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component
@RefreshScope
class DeployServerCustomerConfiguration @Autowired constructor(cloudTools: CloudTools) {
    var serverIp: String = cloudTools.getServerIp()

    var serverPort: Int = cloudTools.getServerPort()

    @Value("\${deploy-server.upload-path}")
    var uploadPath: String = ""
}
