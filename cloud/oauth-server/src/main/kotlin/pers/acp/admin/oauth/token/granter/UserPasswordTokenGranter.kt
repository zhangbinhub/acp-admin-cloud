package pers.acp.admin.oauth.token.granter

import pers.acp.core.CommonTools
import pers.acp.core.security.Sha256Encrypt
import pers.acp.admin.oauth.constant.OauthConstant
import pers.acp.admin.oauth.token.error.CustomerOAuth2Exception
import org.springframework.security.authentication.*
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException
import org.springframework.security.oauth2.provider.*
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices
import pers.acp.admin.oauth.token.UserPasswordAuthenticationToken
import kotlin.jvm.Throws

class UserPasswordTokenGranter(
    private val authenticationManager: AuthenticationManager,
    tokenServices: AuthorizationServerTokenServices,
    clientDetailsService: ClientDetailsService,
    private val userDetailsService: UserDetailsService,
    requestFactory: OAuth2RequestFactory
) : AbstractTokenGranter(tokenServices, clientDetailsService, requestFactory, grantType) {
    companion object {
        private const val grantType = OauthConstant.granterUserPassword
        private const val offset = 1
    }

    @Throws(CustomerOAuth2Exception::class)
    override fun getOAuth2Authentication(client: ClientDetails?, tokenRequest: TokenRequest): OAuth2Authentication {
        val parameters = tokenRequest.requestParameters.toMutableMap()
        val username = parameters["username"] ?: throw CustomerOAuth2Exception("用户名不能为空！")
        val password = parameters["password"] ?: throw CustomerOAuth2Exception("密码不能为空！")
        // Protect from downstream leaks of password
        parameters.remove("password")
        try {
            val user = userDetailsService.loadUserByUsername(username)
            if (!user.isEnabled) {
                throw CustomerOAuth2Exception("用户已被锁定或禁用！")
            }
            if (!matches(password, user.password)) {
                throw CustomerOAuth2Exception("用户名或密码不正确！")
            }
            return UserPasswordAuthenticationToken(username, null, user.authorities).apply {
                this.details = parameters
            }.let {
                authenticationManager.authenticate(it)
            }.apply {
                if (this == null || !this.isAuthenticated) {
                    throw InvalidGrantException("Could not authenticate user: $username")
                }
            }.let {
                OAuth2Authentication(requestFactory.createOAuth2Request(client, tokenRequest), it)
            }
        } catch (e: UsernameNotFoundException) {
            throw CustomerOAuth2Exception("用户名或密码不正确！")
        } catch (t: Throwable) {
            throw CustomerOAuth2Exception(t.message)
        }
    }

    /**
     * 密码验证
     * @param rawPassword 用户输入的密码
     * @param userPassword 系统存储的密码
     */
    private fun matches(rawPassword: String, userPassword: String): Boolean =
        CommonTools.getNowDateTime().let { now ->
            for (o in -offset..offset) {
                val password =
                    Sha256Encrypt.encrypt(userPassword + CommonTools.getDateTimeString(now.plusHours(o), "yyyyMMddHH"))
                if (rawPassword.equals(password, ignoreCase = true)) {
                    return true
                }
            }
            false
        }
}