package pers.acp.admin.oauth.token.granter

import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.SpringSecurityMessageSource
import org.springframework.stereotype.Component
import pers.acp.admin.oauth.security.SecurityUserDetailsService
import pers.acp.admin.oauth.token.UserPasswordAuthenticationToken
import pers.acp.admin.oauth.token.error.CustomerOAuth2Exception
import pers.acp.core.CommonTools
import pers.acp.core.security.Sha256Encrypt

@Component
class UserPasswordAuthenticationProvider(
    private val userDetailsService: SecurityUserDetailsService
) : AuthenticationProvider, MessageSourceAware {
    companion object {
        private const val offset = 1
    }

    private var messages: MessageSourceAccessor = SpringSecurityMessageSource.getAccessor()
    override fun authenticate(authentication: Authentication?): Authentication? {
        if (!supports(authentication?.javaClass)) {
            return null
        }
        return authentication!!.name?.let { username ->
            userDetailsService.loadUserByUsername(username).let { user ->
                if (!user.isEnabled) {
                    throw CustomerOAuth2Exception("用户已被锁定或禁用！")
                }
                authentication.credentials?.toString()?.let { password ->
                    if (!matches(password, user.password)) {
                        userDetailsService.storePasswordErrorTime(username)
                        throw CustomerOAuth2Exception("用户名或密码不正确！")
                    } else {
                        userDetailsService.clearPasswordErrorTime(username)
                    }
                    UserPasswordAuthenticationToken(username, null, user.authorities).apply {
                        this.isAuthenticated = user.authorities.isNotEmpty()
                        this.details = authentication.details
                    }
                } ?: throw CustomerOAuth2Exception("密码不能为空！")
            }
        } ?: throw CustomerOAuth2Exception("用户名不能为空！")
    }

    override fun supports(authentication: Class<*>?): Boolean = authentication?.let {
        UserPasswordAuthenticationToken::class.java.isAssignableFrom(it)
    } ?: false

    override fun setMessageSource(messageSource: MessageSource) {
        messages = MessageSourceAccessor(messageSource)
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