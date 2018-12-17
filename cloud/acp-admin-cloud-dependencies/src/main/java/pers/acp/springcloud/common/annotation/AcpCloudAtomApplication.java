package pers.acp.springcloud.common.annotation;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.annotation.*;

/**
 * @author zhangbin by 12/04/2018 12:47
 * @since JDK 11
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringCloudApplication
@EnableFeignClients
@EnableHystrix
@EnableOAuth2Sso
@EnableSwagger2
public @interface AcpCloudAtomApplication {
}
