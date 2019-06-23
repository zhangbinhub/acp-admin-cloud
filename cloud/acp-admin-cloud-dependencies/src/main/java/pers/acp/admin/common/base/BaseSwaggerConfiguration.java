package pers.acp.admin.common.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.acp.spring.boot.conf.SwaggerConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Component
public class BaseSwaggerConfiguration {

    private final SwaggerConfiguration swaggerConfiguration;

    @Value("${info.version}")
    private String version;

    @Autowired
    public BaseSwaggerConfiguration(SwaggerConfiguration swaggerConfiguration) {
        this.swaggerConfiguration = swaggerConfiguration;
    }

    private List<Parameter> globalOperationParameters() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("Authorization").description("认证信息").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        return pars;
    }

    private ApiInfo buildApiInfo(String title) {
        return new ApiInfoBuilder()
                //页面标题
                .title(title)
                //创建人
                .contact(new Contact("ZhangBin", "https://github.com/zhangbin1010", "zhangbin1010@qq.com"))
                //版本号
                .version(version)
                //描述
                .description("API Document")
                .build();
    }

    protected Docket buildDocket(String basePackage, String title) {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerConfiguration.isEnabled())
                .apiInfo(buildApiInfo(title))
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(globalOperationParameters());
    }

}
