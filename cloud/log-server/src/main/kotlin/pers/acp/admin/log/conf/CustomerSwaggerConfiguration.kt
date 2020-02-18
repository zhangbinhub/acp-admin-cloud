package pers.acp.admin.log.conf

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pers.acp.admin.common.base.BaseSwaggerConfiguration
import pers.acp.spring.boot.conf.SwaggerConfiguration

/**
 * @author zhang by 27/12/2018
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class CustomerSwaggerConfiguration @Autowired
constructor(@Value("\${info.version}")
            version: String?,
            swaggerConfiguration: SwaggerConfiguration) : BaseSwaggerConfiguration(version, swaggerConfiguration) {

    @Bean
    fun createRestApi() = buildDocket("pers.acp.admin.log.controller", "Log Server RESTful API")

}
