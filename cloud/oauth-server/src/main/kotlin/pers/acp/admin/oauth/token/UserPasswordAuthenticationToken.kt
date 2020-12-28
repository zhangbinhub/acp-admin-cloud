package pers.acp.admin.oauth.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.SpringSecurityCoreVersion

class UserPasswordAuthenticationToken : AbstractAuthenticationToken {
    private var principal: Any? = null
    private var credentials: Any? = null

    companion object {
        private const val serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID
    }

    constructor(principal: Any?, credentials: Any?) : super(null) {
        this.principal = principal
        this.credentials = credentials
        super.setAuthenticated(false)
    }

    constructor(principal: Any?, credentials: Any?, authorities: Collection<GrantedAuthority>) : super(authorities) {
        this.principal = principal
        this.credentials = credentials
        super.setAuthenticated(true)
    }

    override fun getCredentials(): Any? = credentials
    override fun getPrincipal(): Any? = principal
    override fun eraseCredentials() {
        super.eraseCredentials()
        credentials = null
    }
}