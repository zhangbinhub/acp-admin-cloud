package pers.acp.admin.oauth.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import pers.acp.springboot.core.conf.SwaggerConfiguration;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 27/12/2018
 * @since JDK 11
 */
@Configuration
@EnableSwagger2
@Component
public class CustomerSwaggerConfiguration {

    private final SwaggerConfiguration swaggerConfiguration;

    @Autowired
    public CustomerSwaggerConfiguration(SwaggerConfiguration swaggerConfiguration) {
        this.swaggerConfiguration = swaggerConfiguration;
    }

    @Bean
    public Docket createRestApi() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("Authorization").description("认证信息").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerConfiguration.isEnabled())
                .apiInfo(apiInfo())
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage("pers.acp.admin.oauth.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("Oauth Server RESTful API")
                //创建人
                .contact(new Contact("ZhangBin", "https://github.com/zhangbin1010", "zhangbin1010@qq.com"))
                //版本号
                .version("1.0.0")
                //描述
                .description("API Document")
                .build();
    }

}
