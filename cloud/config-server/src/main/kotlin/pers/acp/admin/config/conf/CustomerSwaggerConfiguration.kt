package pers.acp.admin.config.conf

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import pers.acp.admin.common.base.BaseSwaggerConfiguration
import pers.acp.spring.boot.conf.SwaggerConfiguration
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * @author zhang by 27/12/2018
 * @since JDK 11
 */
@Configuration
@EnableSwagger2
@Component
class CustomerSwaggerConfiguration @Autowired
constructor(@Value("\${info.version}")
            version: String?,
            swaggerConfiguration: SwaggerConfiguration) : BaseSwaggerConfiguration(version, swaggerConfiguration) {

    @Bean
    fun createRestApi(): Docket {
        return buildDocket("pers.acp.admin.config.controller", "Config Server RESTful API")
    }

}
