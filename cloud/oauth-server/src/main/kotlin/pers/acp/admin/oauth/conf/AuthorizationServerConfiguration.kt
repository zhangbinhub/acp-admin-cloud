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
import pers.acp.admin.oauth.token.error.CustomerOAuth2Exception
import pers.acp.admin.oauth.token.error.CustomerWebResponseExceptionTranslator
import pers.acp.admin.oauth.token.granter.UserPasswordTokenGranter
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.CompositeTokenGranter
import org.springframework.security.oauth2.provider.OAuth2RequestFactory
import org.springframework.security.oauth2.provider.TokenGranter
import org.springframework.security.oauth2.provider.TokenRequest
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory

/**
 * @author zhangbin by 11/04/2018 14:34
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@EnableAuthorizationServer
class AuthorizationServerConfiguration @Autowired
constructor(
    private val authenticationManager: AuthenticationManager,
    private val securityClientDetailsDomain: SecurityClientDetailsDomain,
    private val securityTokenService: SecurityTokenService,
    private val customerWebResponseExceptionTranslator: CustomerWebResponseExceptionTranslator
) : AuthorizationServerConfigurerAdapter() {

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        securityTokenService.setClientDetailsService(securityClientDetailsDomain)
        endpoints.authenticationManager(authenticationManager)
            .tokenServices(securityTokenService)
            .tokenEnhancer(securityTokenService.securityTokenEnhancer)
            .tokenStore(securityTokenService.getTokenStore())
            .exceptionTranslator(customerWebResponseExceptionTranslator)
            .tokenGranter(tokenGranter())
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

    private fun getDefaultTokenGranters(): List<TokenGranter> {
        val requestFactory: OAuth2RequestFactory = DefaultOAuth2RequestFactory(securityClientDetailsDomain)
        return mutableListOf<TokenGranter>().apply {
            this.add(
                AuthorizationCodeTokenGranter(
                    securityTokenService,
                    InMemoryAuthorizationCodeServices(),
                    securityClientDetailsDomain,
                    requestFactory
                )
            )
            this.add(RefreshTokenGranter(securityTokenService, securityClientDetailsDomain, requestFactory))
            this.add(ImplicitTokenGranter(securityTokenService, securityClientDetailsDomain, requestFactory))
            this.add(ClientCredentialsTokenGranter(securityTokenService, securityClientDetailsDomain, requestFactory))
            // 自定义用户密码验证
            this.add(
                UserPasswordTokenGranter(
                    authenticationManager,
                    securityTokenService,
                    securityClientDetailsDomain,
                    requestFactory
                )
            )
        }
    }

    private fun tokenGranter(): TokenGranter = object : TokenGranter {
        private var delegate: CompositeTokenGranter? = null

        @Throws(CustomerOAuth2Exception::class)
        override fun grant(grantType: String, tokenRequest: TokenRequest): OAuth2AccessToken {
            if (delegate == null) {
                delegate = CompositeTokenGranter(getDefaultTokenGranters())
            }
            return delegate!!.grant(grantType, tokenRequest) ?: throw CustomerOAuth2Exception("不支持的验证方式！")
        }
    }
}
