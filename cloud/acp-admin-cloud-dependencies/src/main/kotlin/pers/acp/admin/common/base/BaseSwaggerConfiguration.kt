package pers.acp.admin.common.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import pers.acp.spring.boot.conf.SwaggerConfiguration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.service.Parameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Component
class BaseSwaggerConfiguration @Autowired
constructor(private val swaggerConfiguration: SwaggerConfiguration) {

    @Value("\${info.version}")
    private val version: String? = null

    private fun globalOperationParameters(): List<Parameter> {
        val tokenPar = ParameterBuilder()
        val pars: MutableList<Parameter> = mutableListOf()
        tokenPar.name("Authorization").description("认证信息").modelRef(ModelRef("string")).parameterType("header").required(false).build()
        pars.add(tokenPar.build())
        return pars
    }

    private fun buildApiInfo(title: String): ApiInfo =
            ApiInfoBuilder()
                    //页面标题
                    .title(title)
                    //创建人
                    .contact(Contact("ZhangBin", "https://github.com/zhangbin1010", "zhangbin1010@qq.com"))
                    //版本号
                    .version(version)
                    //描述
                    .description("API Document")
                    .build()

    protected fun buildDocket(basePackage: String, title: String): Docket =
            Docket(DocumentationType.SWAGGER_2)
                    .enable(swaggerConfiguration.enabled)
                    .apiInfo(buildApiInfo(title))
                    .select()
                    //为当前包路径
                    .apis(RequestHandlerSelectors.basePackage(basePackage))
                    .paths(PathSelectors.any())
                    .build()
                    .globalOperationParameters(globalOperationParameters())

}
