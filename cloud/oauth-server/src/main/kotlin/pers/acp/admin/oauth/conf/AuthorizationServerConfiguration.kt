package pers.acp.admin.oauth.conf

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import pers.acp.admin.oauth.domain.security.SecurityClientDetailsDomain
import pers.acp.admin.oauth.token.SecurityTokenService
import pers.acp.admin.oauth.domain.security.SecurityUserDetailsDomain

/**
 * @author zhangbin by 11/04/2018 14:34
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableAuthorizationServer
class AuthorizationServerConfiguration @Autowired
constructor(private val authenticationManager: AuthenticationManager,
            private val securityUserDetailsDomain: SecurityUserDetailsDomain,
            private val securityClientDetailsDomain: SecurityClientDetailsDomain,
            private val securityTokenService: SecurityTokenService) : AuthorizationServerConfigurerAdapter() {

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        securityTokenService.setClientDetailsService(securityClientDetailsDomain)
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(securityUserDetailsDomain)
                .tokenServices(securityTokenService)
                .tokenEnhancer(securityTokenService.securityTokenEnhancer)
                .tokenStore(securityTokenService.getTokenStore())
    }

    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients()
    }

    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.withClientDetails(securityClientDetailsDomain)
    }

}
