package pers.acp.admin.oauth.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import java.io.Serializable

class UserPasswordAuthenticationToken : Serializable, AbstractAuthenticationToken {
    private var principal: Any? = null
    private var credentials: Any? = null

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

    companion object {
        private const val serialVersionUID: Long = -8266012108197485218L
    }
}