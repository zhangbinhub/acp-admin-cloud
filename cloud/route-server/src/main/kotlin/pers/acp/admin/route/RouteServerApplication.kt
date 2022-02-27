package pers.acp.admin.route

import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudAtomApplication
import org.springframework.boot.runApplication

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
class RouteServerApplication

fun main(args: Array<String>) {
    runApplication<RouteServerApplication>(*args)
}
