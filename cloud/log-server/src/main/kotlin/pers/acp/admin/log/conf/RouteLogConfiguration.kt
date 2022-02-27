package pers.acp.admin.log.conf

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pers.acp.admin.constant.RouteConstant
import pers.acp.admin.log.consumer.RouteLogConsumer
import pers.acp.admin.log.domain.LogDomain

/**
 * @author zhang by 10/09/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(BindingServiceConfiguration::class)
class RouteLogConfiguration {
    @Bean(RouteConstant.ROUTE_LOG_CONSUMER)
    @ConditionalOnMissingBean(name = [RouteConstant.ROUTE_LOG_CONSUMER])
    fun routeLogConsumer(
        logAdapter: LogAdapter,
        objectMapper: ObjectMapper,
        logDomain: LogDomain,
        logServerCustomerConfiguration: LogServerCustomerConfiguration
    ) = RouteLogConsumer(logAdapter, objectMapper, logDomain, logServerCustomerConfiguration)
}