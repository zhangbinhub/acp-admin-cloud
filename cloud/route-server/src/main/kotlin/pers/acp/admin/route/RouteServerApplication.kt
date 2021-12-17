package pers.acp.admin.route

import org.springframework.boot.runApplication
import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudAtomApplication

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
class RouteServerApplication

fun main(args: Array<String>) {
    runApplication<RouteServerApplication>(*args)
}
