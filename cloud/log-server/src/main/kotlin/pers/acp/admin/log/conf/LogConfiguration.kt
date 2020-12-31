package pers.acp.admin.log.conf

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.cloud.stream.config.BindingProperties
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.config.BindingServiceProperties
import org.springframework.cloud.stream.function.StreamFunctionProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.MimeTypeUtils
import pers.acp.admin.constant.RouteConstant
import pers.acp.admin.log.consumer.RouteLogConsumer
import pers.acp.admin.log.domain.LogDomain
import pers.acp.spring.boot.interfaces.LogAdapter
import javax.annotation.PostConstruct

/**
 * @author zhang by 10/09/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(BindingServiceConfiguration::class)
class LogConfiguration @Autowired
constructor(
    private val bindingServiceProperties: BindingServiceProperties,
    private val streamFunctionProperties: StreamFunctionProperties
) {
    private val routeLogConsumerBindName = "${RouteConstant.ROUTE_LOG_INPUT}-in-0"

    @PostConstruct
    fun init() {
        initConsumer()
    }

    private fun initConsumer() {
        if (this.bindingServiceProperties.bindings[routeLogConsumerBindName] == null) {
            this.bindingServiceProperties.bindings[routeLogConsumerBindName] = BindingProperties()
        }
        if (this.streamFunctionProperties.definition != null && this.streamFunctionProperties.definition.isNotBlank()) {
            this.streamFunctionProperties.definition += ";${RouteConstant.ROUTE_LOG_INPUT}"
        } else {
            this.streamFunctionProperties.definition = RouteConstant.ROUTE_LOG_INPUT
        }
        this.bindingServiceProperties.bindings[routeLogConsumerBindName]?.let {
            if (it.destination == null || it.destination == routeLogConsumerBindName) {
                it.destination = RouteConstant.ROUTE_LOG_DESCRIPTION
            }
            it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
            it.group = RouteConstant.ROUTE_LOG_CONSUMER_GROUP
        }
    }

    @Bean(RouteConstant.ROUTE_LOG_INPUT)
    fun routeLogConsumer(
        logAdapter: LogAdapter,
        objectMapper: ObjectMapper,
        logDomain: LogDomain,
        logServerCustomerConfiguration: LogServerCustomerConfiguration
    ) = RouteLogConsumer(logAdapter, objectMapper, logDomain, logServerCustomerConfiguration)

}