package pers.acp.springcloud.oauth.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import pers.acp.core.CommonTools;
import pers.acp.springcloud.oauth.component.UserPasswordEncoder;
import pers.acp.springcloud.oauth.domain.SecurityClientDetailsService;
import pers.acp.springcloud.oauth.domain.SecurityUserDetailsService;

/**
 * @author zhangbin by 11/04/2018 15:16
 * @since JDK 11
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final String contextPath;

    private final UserPasswordEncoder userPasswordEncoder;

    @Autowired
    public WebSecurityConfiguration(ServerProperties serverProperties, UserPasswordEncoder userPasswordEncoder) {
        this.userPasswordEncoder = userPasswordEncoder;
        this.contextPath = CommonTools.isNullStr(serverProperties.getServlet().getContextPath()) ? "" : serverProperties.getServlet().getContextPath();
    }

    @Bean(name = "customerUserDetailsService")
    public UserDetailsService userDetailsService() {
        return new SecurityUserDetailsService();
    }

    @Bean(name = "customerClientDetailsService")
    public ClientDetailsService clientDetailsService() {
        return new SecurityClientDetailsService();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(userPasswordEncoder);
    }

    /**
     * http 验证策略配置
     *
     * @param http http 安全验证对象
     * @throws Exception 异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers(
                contextPath + "/error",
                contextPath + "/download",
                contextPath + "/actuator",
                contextPath + "/actuator/**",
                contextPath + "/oauth/**").permitAll()
                .anyRequest().authenticated();
    }

}
