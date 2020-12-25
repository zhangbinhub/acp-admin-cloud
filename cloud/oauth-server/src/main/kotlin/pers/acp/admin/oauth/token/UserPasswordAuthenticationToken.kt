package pers.acp.admin.oauth.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.SpringSecurityCoreVersion

class UserPasswordAuthenticationToken(
    private val principal: Any?,
    private var credentials: Any?,
    authorities: Collection<GrantedAuthority>?
) : AbstractAuthenticationToken(authorities) {
    companion object {
        private const val serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID
    }

    init {
        super.setAuthenticated(true)
    }

    override fun getCredentials(): Any? = credentials
    override fun getPrincipal(): Any? = principal
    override fun eraseCredentials() {
        super.eraseCredentials()
        credentials = null
    }
}