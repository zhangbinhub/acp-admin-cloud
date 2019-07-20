package pers.acp.admin.gateway

import org.springframework.boot.runApplication
import org.springframework.cloud.client.SpringCloudApplication

/**
 * @author zhangbin by 2018-3-10 20:45
 * @since JDK 11
 */
@SpringCloudApplication
class GateWayServerApplication

fun main(args: Array<String>) {
    runApplication<GateWayServerApplication>(*args)
}
