package pers.acp.admin.log

import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudAtomApplication
import org.springframework.boot.runApplication

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
class LogServerApplication

fun main(args: Array<String>) {
    runApplication<LogServerApplication>(*args)
}
