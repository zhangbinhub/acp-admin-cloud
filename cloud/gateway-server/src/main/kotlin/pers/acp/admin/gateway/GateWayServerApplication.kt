package pers.acp.admin.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author zhangbin by 2018-3-10 20:45
 * @since JDK 11
 */
@SpringBootApplication
class GateWayServerApplication

fun main(args: Array<String>) {
    runApplication<GateWayServerApplication>(*args)
}
