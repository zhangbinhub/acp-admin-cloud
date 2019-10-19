package pers.acp.admin.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

/**
 * @author zhangbin by 2018-3-10 20:45
 * @since JDK 11
 */
@SpringBootApplication
@EnableDiscoveryClient
class GateWayServerApplication

fun main(args: Array<String>) {
    runApplication<GateWayServerApplication>(*args)
}
