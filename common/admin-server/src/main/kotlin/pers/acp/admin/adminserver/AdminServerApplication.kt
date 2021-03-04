package pers.acp.admin.adminserver

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author zhangbin by 2018-3-11 10:50
 * @since JDK 11
 */
@SpringBootApplication
@EnableAdminServer
class AdminServerApplication

fun main(args: Array<String>) {
    runApplication<AdminServerApplication>(*args)
}