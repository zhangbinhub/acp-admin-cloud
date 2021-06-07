package pers.acp.admin.route.conf

import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pers.acp.admin.constant.RouteConstant
import pers.acp.admin.route.producer.UpdateRouteBridge

/**
 * @author zhang by 17/05/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(BindingServiceConfiguration::class)
class RouteConfiguration {
    @Bean
    fun updateRouteBridge(streamBridge: StreamBridge) =
        UpdateRouteBridge(streamBridge, RouteConstant.UPDATE_ROUTE_OUTPUT)
}
