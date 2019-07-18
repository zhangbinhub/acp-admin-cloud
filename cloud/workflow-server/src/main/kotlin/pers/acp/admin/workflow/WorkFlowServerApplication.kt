package pers.acp.admin.workflow

import org.springframework.boot.SpringApplication
import org.springframework.boot.runApplication
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
class WorkFlowServerApplication

fun main(args: Array<String>) {
    runApplication<WorkFlowServerApplication>(*args)
}