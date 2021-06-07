package pers.acp.admin.route.conf

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import org.springframework.util.MimeTypeUtils
import pers.acp.admin.constant.RouteConstant

/**
 * @author zhang by 17/05/2019
 * @since JDK 11
 */
class RouteUpdateEnvironmentPostProcessor : EnvironmentPostProcessor {
    private val defaultPropertiesName = "acpCloudRouteDefaultProperties"

    override fun postProcessEnvironment(environment: ConfigurableEnvironment?, application: SpringApplication?) {
        val default: MutableMap<String, Any> = HashMap()
        "spring.cloud.stream.bindings.${RouteConstant.UPDATE_ROUTE_OUTPUT}".let { prefix ->
            environment!!.getProperty("$prefix.destination", RouteConstant.UPDATE_ROUTE_DESCRIPTION)
                .let { destination ->
                    if (destination == RouteConstant.UPDATE_ROUTE_OUTPUT) {
                        RouteConstant.UPDATE_ROUTE_DESCRIPTION
                    } else {
                        destination
                    }
                }.let { destination ->
                    default["$prefix.destination"] = destination
                    default["$prefix.contentType"] = MimeTypeUtils.APPLICATION_JSON_VALUE
                    addOrReplace(environment.propertySources, default)
                }
        }
    }

    private fun addOrReplace(propertySources: MutablePropertySources, map: Map<String, Any?>) {
        var target: MapPropertySource? = null
        if (propertySources.contains(defaultPropertiesName)) {
            val source = propertySources[defaultPropertiesName]
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
            target = MapPropertySource(defaultPropertiesName, map)
        }
        if (!propertySources.contains(defaultPropertiesName)) {
            propertySources.addLast(target)
        }
    }
}
