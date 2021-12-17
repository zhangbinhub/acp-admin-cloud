package pers.acp.admin.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pers.acp.admin.common.hystrix.CommonOauthServerHystrix
import pers.acp.admin.common.hystrix.WorkFlowServerHystrix
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter

/**
 * @author zhang by 12/12/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableFeignClients
@AutoConfigureAfter(BlockingLoadBalancerClientAutoConfiguration::class)
class AcpAdminFeignClientAutoConfiguration {
    @Bean
    fun commonOauthServerHystrix(logAdapter: LogAdapter, objectMapper: ObjectMapper): CommonOauthServerHystrix =
        CommonOauthServerHystrix(logAdapter, objectMapper)

    @Bean
    fun workFlowServerHystrix(logAdapter: LogAdapter, objectMapper: ObjectMapper): WorkFlowServerHystrix =
        WorkFlowServerHystrix(logAdapter, objectMapper)
}