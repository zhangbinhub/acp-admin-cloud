package pers.acp.admin.oauth.conf

import io.github.zhangbinhub.acp.boot.base.BaseSwaggerConfiguration
import io.github.zhangbinhub.acp.boot.conf.SwaggerConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.spring.web.plugins.Docket

/**
 * @author zhang by 27/12/2018
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
class CustomerSwaggerConfiguration @Autowired
constructor(
    @Value("\${info.version}")
    version: String?,
    swaggerConfiguration: SwaggerConfiguration
) : BaseSwaggerConfiguration(version, swaggerConfiguration) {
    @Bean
    fun gatewayApi(): Docket = buildDocket(
        "pers.acp.admin.oauth.controller.api",
        "Oauth Server API",
        "API Document",
        "ZhangBin",
        "https://github.com/zhangbinhub",
        "zhangbin1010@qq.com"
    ).groupName("网关接口")
        .globalRequestParameters(globalRequestParameter())

    @Bean
    fun innerApi(): Docket = buildDocket(
        "pers.acp.admin.oauth.controller.inner",
        "Oauth Server API",
        "API Document",
        "ZhangBin",
        "https://github.com/zhangbinhub",
        "zhangbin1010@qq.com"
    ).groupName("内部接口")
        .globalRequestParameters(globalRequestParameter())

    @Bean
    fun openInnerApi(): Docket = buildDocket(
        "pers.acp.admin.oauth.controller.open.inner",
        "Oauth Server API",
        "API Document",
        "ZhangBin",
        "https://github.com/zhangbinhub",
        "zhangbin1010@qq.com"
    ).groupName("内部开放接口")
}
