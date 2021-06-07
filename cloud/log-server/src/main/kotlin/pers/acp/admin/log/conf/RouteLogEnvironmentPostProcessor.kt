package pers.acp.admin.log.conf

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.cloud.function.context.FunctionProperties
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.util.MimeTypeUtils
import pers.acp.admin.constant.RouteConstant

/**
 * @author zhang by 10/09/2019
 * @since JDK 11
 */
class RouteLogEnvironmentPostProcessor : EnvironmentPostProcessor {
    private val overridePropertiesName = "acpCloudRouteOverrideProperties"
    private val defaultPropertiesName = "acpCloudRouteDefaultProperties"
    private val functionDefinitionProperties = "${FunctionProperties.PREFIX}.definition"
    private val routeLogConsumerBindName = "${RouteConstant.ROUTE_LOG_CONSUMER}-in-0"

    override fun postProcessEnvironment(environment: ConfigurableEnvironment?, application: SpringApplication?) {
        val overrides: MutableMap<String, Any> = HashMap()
        var definition = RouteConstant.ROUTE_LOG_CONSUMER
        if (environment!!.containsProperty(functionDefinitionProperties)) {
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
            default["$prefix.function.bindings.$routeLogConsumerBindName"] = RouteConstant.ROUTE_LOG_INPUT
            val destination = RouteConstant.ROUTE_LOG_DESCRIPTION
            default["$prefix.bindings.${RouteConstant.ROUTE_LOG_INPUT}.destination"] = destination
            default["$prefix.bindings.${RouteConstant.ROUTE_LOG_INPUT}.contentType"] =
                MimeTypeUtils.APPLICATION_JSON_VALUE
            default["$prefix.bindings.${RouteConstant.ROUTE_LOG_INPUT}.group"] = RouteConstant.ROUTE_LOG_CONSUMER_GROUP
            addOrReplace(environment.propertySources, default, defaultPropertiesName, false)
        }
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