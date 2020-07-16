package pers.acp.admin.deploy.conf

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component
@RefreshScope
class DeployServerCustomerConfiguration {
    @Value("\${server.address}")
    var serverIp: String = ""

    @Value("\${server.port}")
    var serverPort: Int = 0

    @Value("\${deploy-server.upload-path}")
    var uploadPath: String = ""
}
