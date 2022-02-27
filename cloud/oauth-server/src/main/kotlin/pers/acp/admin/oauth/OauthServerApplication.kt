package pers.acp.admin.oauth

import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudOauthServerApplication
import org.springframework.boot.runApplication

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudOauthServerApplication
class OauthServerApplication

fun main(args: Array<String>) {
    runApplication<OauthServerApplication>(*args)
}