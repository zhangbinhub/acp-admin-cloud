package pers.acp.admin.adminserver

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.runApplication
import org.springframework.cloud.client.SpringCloudApplication
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard
import org.springframework.cloud.netflix.turbine.EnableTurbine

/**
 * @author zhangbin by 2018-3-11 10:50
 * @since JDK 11
 */
@SpringCloudApplication
@EnableTurbine
@EnableHystrixDashboard
@EnableAdminServer
class AdminServerApplication

fun main(args: Array<String>) {
    runApplication<AdminServerApplication>(*args)
}