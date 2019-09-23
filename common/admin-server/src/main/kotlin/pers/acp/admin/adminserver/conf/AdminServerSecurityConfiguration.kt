package pers.acp.admin.adminserver.conf

import de.codecentric.boot.admin.server.config.AdminServerProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

/**
 * @author zhangbin by 14/09/2018 13:14
 * @since JDK 11
 */
@Configuration
class AdminServerSecurityConfiguration @Autowired
constructor(adminServerProperties: AdminServerProperties) : WebSecurityConfigurerAdapter() {

    private val adminContextPath: String = adminServerProperties.contextPath

    /**
     * http 验证策略配置
     *
     * @param http http 安全验证对象
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        val successHandler = SavedRequestAwareAuthenticationSuccessHandler()
        successHandler.setTargetUrlParameter("redirectTo")
        successHandler.setDefaultTargetUrl("$adminContextPath/")
        http.csrf().disable().authorizeRequests()
                .antMatchers("$adminContextPath/assets/**",
                        "$adminContextPath/instances",
                        "$adminContextPath/instances/**",
                        "$adminContextPath/actuator",
                        "$adminContextPath/actuator/**",
                        "$adminContextPath/login",
                        "$adminContextPath/error",
                        "$adminContextPath/webjars/**",
                        "$adminContextPath/notifications/**",
                        "$adminContextPath/proxy.stream").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage("$adminContextPath/login").successHandler(successHandler).and()
                .logout().logoutUrl("$adminContextPath/logout")
    }

}
