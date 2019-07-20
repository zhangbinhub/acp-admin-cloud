package pers.acp.admin.config.refresh

import org.springframework.boot.runApplication
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication

/**
 * @author zhang by 27/02/2019
 * @since JDK 11
 */
@AcpCloudAtomApplication
class ConfigRefreshServerApplication

fun main(args: Array<String>) {
    runApplication<ConfigRefreshServerApplication>(*args)
}
