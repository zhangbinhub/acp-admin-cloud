package pers.acp.springcloud.oauth.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import pers.acp.core.CommonTools;

/**
 * @author zhangbin by 11/04/2018 15:13
 * @since JDK 11
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final String contextPath;

    @Autowired
    public ResourceServerConfiguration(ServerProperties serverProperties) {
        this.contextPath = CommonTools.isNullStr(serverProperties.getServlet().getContextPath()) ? "" : serverProperties.getServlet().getContextPath();
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
                contextPath + "/oauth/**").permitAll()
                .anyRequest().authenticated();
    }

}
