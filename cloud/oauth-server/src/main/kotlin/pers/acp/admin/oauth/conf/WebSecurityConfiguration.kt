package pers.acp.admin.oauth.conf

import io.github.zhangbinhub.acp.cloud.constant.CloudConfigurationOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import pers.acp.admin.oauth.token.granter.UserPasswordAuthenticationProvider

/**
 * @author zhangbin by 11/04/2018 15:16
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@Order(CloudConfigurationOrder.resourceServerConfiguration + 1)
class WebSecurityConfiguration @Autowired
constructor(
    private val userPasswordAuthenticationProvider: UserPasswordAuthenticationProvider
) : WebSecurityConfigurerAdapter() {
    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(userPasswordAuthenticationProvider)
    }
}
