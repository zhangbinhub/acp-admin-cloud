package pers.acp.admin.route.conf

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.config.BindingProperties
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.config.BindingServiceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.MimeTypeUtils
import pers.acp.admin.route.constant.RouteConstant
import pers.acp.admin.route.producer.UpdateRouteOutput
import pers.acp.admin.route.producer.instance.UpdateRouteProducer

import javax.annotation.PostConstruct

/**
 * @author zhang by 17/05/2019
 * @since JDK 11
 */
@Configuration
@AutoConfigureBefore(BindingServiceConfiguration::class)
@EnableBinding(UpdateRouteOutput::class)
class RouteConfiguration @Autowired
constructor(private val bindings: BindingServiceProperties) {

    @PostConstruct
    fun init() {
        initProducer()
    }

    private fun initProducer() {
        if (this.bindings.bindings[RouteConstant.UPDATE_GATEWAY_OUTPUT] == null) {
            this.bindings.bindings[RouteConstant.UPDATE_GATEWAY_OUTPUT] = BindingProperties()
        }
        this.bindings.bindings[RouteConstant.UPDATE_GATEWAY_OUTPUT]?.let {
            if (it.destination == null || it.destination == RouteConstant.UPDATE_GATEWAY_OUTPUT) {
                it.destination = RouteConstant.UPDATE_ROUTE_DESCRIPTION
            }
            it.contentType = MimeTypeUtils.APPLICATION_JSON_VALUE
        }
    }


    @Bean
    fun updateRouteProducer(updateRouteOutput: UpdateRouteOutput) = UpdateRouteProducer(updateRouteOutput)

}
