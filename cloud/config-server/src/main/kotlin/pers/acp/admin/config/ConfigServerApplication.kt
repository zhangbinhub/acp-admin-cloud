package pers.acp.admin.config

import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication

/**
 * @author zhang by 27/02/2019
 * @since JDK 11
 */
@AcpCloudAtomApplication
@EnableConfigServer
class ConfigServerApplication

fun main(args: Array<String>) {
    runApplication<ConfigServerApplication>(*args)
}
