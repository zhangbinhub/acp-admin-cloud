package pers.acp.admin.common

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pers.acp.admin.common.hystrix.CommonOauthServerHystrix
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 12/12/2019
 * @since JDK 11
 */
@Configuration
@EnableFeignClients
class AcpAdminFeignClientAutoConfiguration {
    @Bean
    fun commonOauthServerHystrix(logAdapter: LogAdapter): CommonOauthServerHystrix =
            CommonOauthServerHystrix(logAdapter)
}