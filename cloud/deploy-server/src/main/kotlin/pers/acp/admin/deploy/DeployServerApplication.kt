package pers.acp.admin.deploy

import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudAtomApplication
import org.springframework.boot.runApplication

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
class DeployServerApplication

fun main(args: Array<String>) {
    runApplication<DeployServerApplication>(*args)
}