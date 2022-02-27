package pers.acp.admin.gateway.conf

import io.github.zhangbinhub.acp.core.CommonTools
import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.cloud.function.context.FunctionProperties
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.util.MimeTypeUtils
import pers.acp.admin.constant.RouteConstant
import pers.acp.admin.gateway.constant.GateWayConstant

/**
 * @author zhang by 17/12/2018 00:41
 * @since JDK 11
 */
class RouteEnvironmentPostProcessor : EnvironmentPostProcessor {
    private val overridePropertiesName = "acpCloudRouteOverrideProperties"
    private val defaultPropertiesName = "acpCloudRouteDefaultProperties"
    private val functionDefinitionProperties = "${FunctionProperties.PREFIX}.definition"
    private val updateRouteConsumerBindName = "${RouteConstant.UPDATE_ROUTE_CONSUMER}-in-0"

    private fun initConsumer(environment: ConfigurableEnvironment) {
        val overrides: MutableMap<String, Any> = HashMap()
        var definition = RouteConstant.UPDATE_ROUTE_CONSUMER
        if (environment.containsProperty(functionDefinitionProperties)) {
            val property = environment.getProperty(functionDefinitionProperties)
            if (property != null && property.contains(definition)) {
                return
            }
            definition = "$property;$definition"
        }
        overrides[functionDefinitionProperties] = definition
        addOrReplace(environment.propertySources, overrides, overridePropertiesName, true)

        val default: MutableMap<String, Any> = HashMap()
        "spring.cloud.stream".let { prefix ->
            default["$prefix.function.bindings.$updateRouteConsumerBindName"] = RouteConstant.UPDATE_ROUTE_INPUT
            val destination = RouteConstant.UPDATE_ROUTE_DESCRIPTION
            val groupId = GateWayConstant.UPDATE_ROUTE_GROUP_PREFIX + CommonTools.getUuid32()
            default["$prefix.bindings.${RouteConstant.UPDATE_ROUTE_INPUT}.destination"] = destination
            default["$prefix.bindings.${RouteConstant.UPDATE_ROUTE_INPUT}.contentType"] =
                MimeTypeUtils.APPLICATION_JSON_VALUE
            default["$prefix.bindings.${RouteConstant.UPDATE_ROUTE_INPUT}.group"] = groupId
            addOrReplace(environment.propertySources, default, defaultPropertiesName, false)
        }
    }

    private fun initProducer(environment: ConfigurableEnvironment) {
        val default: MutableMap<String, Any> = HashMap()
        "spring.cloud.stream.bindings.${RouteConstant.ROUTE_LOG_OUTPUT}".let { prefix ->
            environment.getProperty("$prefix.destination", RouteConstant.ROUTE_LOG_DESCRIPTION).let { destination ->
                if (destination == RouteConstant.ROUTE_LOG_OUTPUT) {
                    RouteConstant.ROUTE_LOG_DESCRIPTION
                } else {
                    destination
                }
            }.let { destination ->
                default["$prefix.destination"] = destination
                default["$prefix.contentType"] = MimeTypeUtils.APPLICATION_JSON_VALUE
                addOrReplace(environment.propertySources, default, defaultPropertiesName, false)
            }
        }
    }

    override fun postProcessEnvironment(environment: ConfigurableEnvironment?, application: SpringApplication?) {
        initConsumer(environment!!)
        initProducer(environment)
    }

    private fun addOrReplace(
        propertySources: MutablePropertySources, map: Map<String, Any?>,
        propertySourceName: String, first: Boolean
    ) {
        var target: MapPropertySource? = null
        if (propertySources.contains(propertySourceName)) {
            val source = propertySources[propertySourceName]
            if (source is MapPropertySource) {
                target = source
                for (key in map.keys) {
                    if (!target.containsProperty(key)) {
                        target.source[key] = map[key]
                    }
                }
            }
        }
        if (target == null) {
            target = MapPropertySource(propertySourceName, map)
        }
        if (!propertySources.contains(propertySourceName)) {
            if (first) {
                propertySources.addFirst(target)
            } else {
                propertySources.addLast(target)
            }
        }
    }
}
