package pers.acp.admin.file.conf

import org.springframework.beans.factory.annotation.Autowired
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
constructor(swaggerConfiguration: SwaggerConfiguration) : BaseSwaggerConfiguration(swaggerConfiguration) {

    @Bean
    fun createRestApi(): Docket {
        return buildDocket("pers.acp.admin.file.controller", "File Server RESTful API")
    }

}
