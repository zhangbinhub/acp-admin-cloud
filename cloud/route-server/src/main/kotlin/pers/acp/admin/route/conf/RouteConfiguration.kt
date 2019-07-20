package pers.acp.admin.route.conf

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
import pers.acp.admin.route.constant.GateWayConstant
import pers.acp.admin.route.consumer.RouteLogInput
import pers.acp.admin.route.consumer.instance.RouteLogConsumer
import pers.acp.admin.route.domain.RouteLogDomain
import pers.acp.admin.route.producer.UpdateRouteOutput
import pers.acp.admin.route.producer.instance.UpdateRouteProducer

import javax.annotation.PostConstruct

/**
 * @author zhang by 17/05/2019
 * @since JDK 11
 */
@Configuration
@AutoConfigureBefore(BindingServiceConfiguration::class)
@EnableBinding(UpdateRouteOutput::class, RouteLogInput::class)
class RouteConfiguration @Autowired
constructor(private val bindings: BindingServiceProperties) {

    @PostConstruct
    fun init() {
        initProducer()
        initConsumer()
    }

    private fun initProducer() {
        if (this.bindings.bindings[GateWayConstant.UPDATE_GATEWAY_OUTPUT] == null) {
            this.bindings.bindings[GateWayConstant.UPDATE_GATEWAY_OUTPUT] = BindingProperties()
        }
        this.bindings.bindings[GateWayConstant.UPDATE_GATEWAY_OUTPUT]?.let {
            if (it.destination == null || it.destination == GateWayConstant.UPDATE_GATEWAY_OUTPUT) {
                it.destination = GateWayConstant.UPDATE_ROUTE_DESCRIPTION
            }
            it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
        }
    }

    private fun initConsumer() {
        if (this.bindings.bindings[GateWayConstant.ROUTE_LOG_INPUT] == null) {
            this.bindings.bindings[GateWayConstant.ROUTE_LOG_INPUT] = BindingProperties()
        }
        this.bindings.bindings[GateWayConstant.ROUTE_LOG_INPUT]?.let {
            if (it.destination == null || it.destination == GateWayConstant.ROUTE_LOG_INPUT) {
                it.destination = GateWayConstant.ROUTE_LOG_DESCRIPTION
            }
            it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
            it.group = GateWayConstant.ROUTE_LOG_CONSUMER_GROUP
        }
    }

    @Bean
    fun updateRouteProducer(updateRouteOutput: UpdateRouteOutput) = UpdateRouteProducer(updateRouteOutput)

    @Bean
    fun updateRouteConsumer(objectMapper: ObjectMapper, routeLogDomain: RouteLogDomain) = RouteLogConsumer(objectMapper, routeLogDomain)

}
