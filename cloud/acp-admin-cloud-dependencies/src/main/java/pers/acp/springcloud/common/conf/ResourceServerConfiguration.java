package pers.acp.springcloud.common.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import pers.acp.client.exceptions.HttpException;
import pers.acp.client.http.HttpClientBuilder;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springcloud.common.enums.RestPrefix;

/**
 * Oauth2 资源服务配置
 *
 * @author zhangbin by 11/04/2018 15:13
 * @since JDK 11
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final OAuth2ClientProperties clientProperties;

    private final ResourceServerProperties resourceServerProperties;

    private final String contextPath;

    @Autowired
    public ResourceServerConfiguration(OAuth2ClientProperties clientProperties, ResourceServerProperties resourceServerProperties, ServerProperties serverProperties) {
        this.clientProperties = clientProperties;
        this.resourceServerProperties = resourceServerProperties;
        this.contextPath = CommonTools.isNullStr(serverProperties.getServlet().getContextPath()) ? "" : serverProperties.getServlet().getContextPath();
    }

    /**
     * 自定义权限验证服务，远程调用认证服务进行验证
     *
     * @return 远程 token 认证服务实例
     */
    @Primary
    @Bean
    public RemoteTokenServices remoteTokenServices() {
        RemoteTokenServices services = new RemoteTokenServices();
        try {
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(new HttpClientBuilder().build().getHttpClient()));
            // 自定义错误处理类，所有错误放行统一交由 oauth 模块进行进出
            restTemplate.setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) {
                    return false;
                }

                @Override
                public void handleError(ClientHttpResponse response) {

                }
            });
            services.setRestTemplate(restTemplate);
        } catch (HttpException e) {
            log.error(e.getMessage(), e);
        }
        services.setCheckTokenEndpointUrl(resourceServerProperties.getTokenInfoUri());
        services.setClientId(clientProperties.getClientId());
        services.setClientSecret(clientProperties.getClientSecret());
        return services;
    }

    /**
     * 设置 token 验证服务
     *
     * @param resources 资源服务安全验证配置对象
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenServices(remoteTokenServices());
    }

    /**
     * http 验证策略配置
     *
     * @param http http 安全验证对象
     * @throws Exception 异常
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // match 匹配的url，赋予全部权限（不进行拦截）
        http.csrf().disable().authorizeRequests().antMatchers(
                contextPath + "/error",
                contextPath + "/download",
                contextPath + "/actuator",
                contextPath + "/actuator/**",
                contextPath + "/v2/api-docs",
                contextPath + "/configuration/ui",
                contextPath + "/swagger-resources/**",
                contextPath + "/configuration/security",
                contextPath + "/swagger-ui.html",
                contextPath + "/webjars/**",
                contextPath + "/swagger-resources/configuration/ui",
                contextPath + "/hystrix.stream",
                contextPath + RestPrefix.OPEN.getUrlPrefix() + "/**").permitAll()
                .anyRequest().authenticated();
    }

}
