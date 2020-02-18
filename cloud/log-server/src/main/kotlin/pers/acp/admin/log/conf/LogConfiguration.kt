package pers.acp.admin.log.conf

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.config.BindingProperties
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.config.BindingServiceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.MimeTypeUtils
import pers.acp.admin.constant.RouteConstant
import pers.acp.admin.log.consumer.RouteLogInput
import pers.acp.admin.log.consumer.instance.RouteLogConsumer
import pers.acp.admin.log.domain.LogDomain
import pers.acp.spring.boot.interfaces.LogAdapter
import javax.annotation.PostConstruct

/**
 * @author zhang by 10/09/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(BindingServiceConfiguration::class)
@EnableBinding(RouteLogInput::class)
class LogConfiguration @Autowired
constructor(private val bindings: BindingServiceProperties) {

    @PostConstruct
    fun init() {
        initConsumer()
    }

    private fun initConsumer() {
        if (this.bindings.bindings[RouteConstant.ROUTE_LOG_INPUT] == null) {
            this.bindings.bindings[RouteConstant.ROUTE_LOG_INPUT] = BindingProperties()
        }
        this.bindings.bindings[RouteConstant.ROUTE_LOG_INPUT]?.let {
            if (it.destination == null || it.destination == RouteConstant.ROUTE_LOG_INPUT) {
                it.destination = RouteConstant.ROUTE_LOG_DESCRIPTION
            }
            it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
            it.group = RouteConstant.ROUTE_LOG_CONSUMER_GROUP
        }
    }

    @Bean
    fun updateRouteConsumer(logAdapter: LogAdapter,
                            objectMapper: ObjectMapper,
                            logDomain: LogDomain,
                            logServerCustomerConfiguration: LogServerCustomerConfiguration) =
            RouteLogConsumer(logAdapter, objectMapper, logDomain, logServerCustomerConfiguration)

}