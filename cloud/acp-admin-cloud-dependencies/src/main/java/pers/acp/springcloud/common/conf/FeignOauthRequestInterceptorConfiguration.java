package pers.acp.springcloud.common.conf;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign 请求拦截器配置
 *
 * @author zhangbin by 12/04/2018 10:13
 * @since JDK 11
 */
@Configuration
public class FeignOauthRequestInterceptorConfiguration {

    /**
     * 自定义 Feign 请求拦截器，请求之前将 Oauth2 token 信息带入 Request 的 header 进行权限传递
     *
     * @return 自定义 Feign 请求拦截器实例
     */
    @Primary
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // 获取当前服务的 request 对象，将 header 中的 Authorization 传递给 feign 的 request 对象
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            if (sra != null) {
                HttpServletRequest request = sra.getRequest();
                template.header("Authorization", request.getHeader("Authorization"));
            }
        };
    }

}
