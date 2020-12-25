package pers.acp.admin.oauth.token.granter

import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceAware
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.SpringSecurityMessageSource
import org.springframework.stereotype.Component
import pers.acp.admin.oauth.token.UserPasswordAuthenticationToken

@Component
class UserPasswordAuthenticationProvider : AuthenticationProvider, MessageSourceAware {
    private var messages: MessageSourceAccessor = SpringSecurityMessageSource.getAccessor()
    override fun authenticate(authentication: Authentication?): Authentication? {
        if (!supports(authentication?.javaClass)) {
            return null
        }
        return authentication
    }

    override fun supports(authentication: Class<*>?): Boolean = authentication?.let {
        UserPasswordAuthenticationToken::class.java.isAssignableFrom(it)
    } ?: false

    override fun setMessageSource(messageSource: MessageSource) {
        messages = MessageSourceAccessor(messageSource)
    }
}