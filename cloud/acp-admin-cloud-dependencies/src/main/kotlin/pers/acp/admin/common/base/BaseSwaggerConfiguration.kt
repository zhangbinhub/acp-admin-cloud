package pers.acp.admin.common.base

import io.github.zhangbinhub.acp.boot.conf.SwaggerConfiguration
import springfox.documentation.builders.*
import springfox.documentation.schema.ModelSpecification
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.service.RequestParameter
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
abstract class BaseSwaggerConfiguration(
    private val version: String?,
    private val swaggerConfiguration: SwaggerConfiguration
) {

    private fun globalOperationParameters(): List<RequestParameter> {
        val tokenPar = RequestParameterBuilder()
        val pars: MutableList<RequestParameter> = mutableListOf()
        tokenPar.name("Authorization").description("认证信息").required(false)
            .`in`("header")
            .contentModel(
                ModelSpecification(
                    "string",
                    null, null, null, null, null, null
                )
            )
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
            .globalRequestParameters(globalOperationParameters())
}
