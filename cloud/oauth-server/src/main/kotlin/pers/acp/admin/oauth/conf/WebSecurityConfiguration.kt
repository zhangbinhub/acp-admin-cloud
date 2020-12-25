package pers.acp.admin.oauth.conf

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import pers.acp.core.CommonTools
import pers.acp.admin.oauth.component.UserPasswordEncoder
import pers.acp.admin.oauth.domain.security.SecurityUserDetailsDomain
import pers.acp.admin.oauth.token.granter.UserPasswordAuthenticationProvider
import pers.acp.spring.cloud.constant.CloudConfigurationOrder

/**
 * @author zhangbin by 11/04/2018 15:16
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@Order(CloudConfigurationOrder.resourceServerConfiguration + 1)
class WebSecurityConfiguration @Autowired
constructor(
    serverProperties: ServerProperties,
    private val userPasswordAuthenticationProvider: UserPasswordAuthenticationProvider,
    private val userPasswordEncoder: UserPasswordEncoder,
    private val userDetailsService: SecurityUserDetailsDomain
) : WebSecurityConfigurerAdapter() {

    private val contextPath: String =
        if (CommonTools.isNullStr(serverProperties.servlet.contextPath)) "" else serverProperties.servlet.contextPath

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(userPasswordEncoder)
        auth.authenticationProvider(userPasswordAuthenticationProvider)
    }

    /**
     * http 验证策略配置
     *
     * @param http http 安全验证对象
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable().authorizeRequests().antMatchers(
            "$contextPath/error",
            "$contextPath/actuator",
            "$contextPath/actuator/**",
            "$contextPath/oauth/authorize",
            "$contextPath/oauth/token",
            "$contextPath/oauth/check_token",
            "$contextPath/oauth/confirm_access",
            "$contextPath/oauth/error"
        ).permitAll().anyRequest().authenticated()
    }

}
