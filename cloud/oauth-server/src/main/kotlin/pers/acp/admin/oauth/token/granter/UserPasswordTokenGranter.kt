package pers.acp.admin.oauth.token.granter

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException
import org.springframework.security.oauth2.provider.*
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices
import pers.acp.admin.oauth.constant.OauthConstant
import pers.acp.admin.oauth.token.UserPasswordAuthenticationToken
import pers.acp.admin.oauth.token.error.CustomerOAuth2Exception

class UserPasswordTokenGranter(
    private val authenticationManager: AuthenticationManager,
    tokenServices: AuthorizationServerTokenServices,
    clientDetailsService: ClientDetailsService,
    requestFactory: OAuth2RequestFactory
) : AbstractTokenGranter(tokenServices, clientDetailsService, requestFactory, grantType) {
    companion object {
        private const val grantType = OauthConstant.granterUserPassword
    }

    @Throws(CustomerOAuth2Exception::class)
    override fun getOAuth2Authentication(client: ClientDetails?, tokenRequest: TokenRequest): OAuth2Authentication =
        tokenRequest.requestParameters.toMutableMap().let { parameters ->
            try {
                val username = parameters["username"]
                val password = parameters["password"]
                parameters.remove("password")
                UserPasswordAuthenticationToken(username, password).apply {
                    this.details = parameters
                }.let {
                    authenticationManager.authenticate(it)
                }?.apply {
                    if (!this.isAuthenticated) {
                        throw InvalidGrantException("用户无权限: $username")
                    }
                }?.let {
                    OAuth2Authentication(requestFactory.createOAuth2Request(client, tokenRequest), it)
                } ?: throw InvalidGrantException("用户无权限: $username")
            } catch (e: UsernameNotFoundException) {
                throw CustomerOAuth2Exception("用户名或密码不正确！")
            } catch (t: Throwable) {
                throw CustomerOAuth2Exception(t.message)
            }
        }
}