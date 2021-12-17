package pers.acp.admin.oauth.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationException
import org.springframework.stereotype.Component
import io.github.zhangbinhub.acp.core.CommonTools
import pers.acp.admin.oauth.constant.OauthConstant
import pers.acp.admin.oauth.domain.ApplicationDomain
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter

import javax.annotation.PostConstruct

/**
 * @author zhangbin by 11/04/2018 15:21
 * @since JDK 11
 */
@Component
class SecurityClientDetailsService @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val applicationDomain: ApplicationDomain
) : ClientDetailsService {

    private var clientDetailsService: ClientDetailsService? = null

    /**
     * 初始化客户端信息
     */
    @PostConstruct
    fun loadClientInfo() = applicationDomain.getAppList().let { applicationList ->
        val memoryClientDetailsServiceBuilder = InMemoryClientDetailsServiceBuilder()
        if (applicationList.size > 0) {
            var builder: ClientDetailsServiceBuilder<InMemoryClientDetailsServiceBuilder>.ClientBuilder? = null
            applicationList.forEach { application ->
                builder = if (builder != null) {
                    builder!!.and().withClient(application.id)
                } else {
                    memoryClientDetailsServiceBuilder.withClient(application.id)
                }
                builder!!.secret(application.secret)
                    .authorizedGrantTypes("client_credentials", "refresh_token", OauthConstant.granterUserPassword)
                    .accessTokenValiditySeconds(application.accessTokenValiditySeconds)
                    .refreshTokenValiditySeconds(application.refreshTokenValiditySeconds)
                application.scope?.apply {
                    if (!CommonTools.isNullStr(this)) {
                        this.split(",").forEach { scope -> builder!!.scopes(scope) }
                    }
                }
            }
        }
        try {
            clientDetailsService = memoryClientDetailsServiceBuilder.build()
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }
    }

    @Throws(ClientRegistrationException::class)
    override fun loadClientByClientId(clientId: String): ClientDetails? =
        if (clientDetailsService != null) {
            clientDetailsService!!.loadClientByClientId(clientId)
        } else {
            null
        }

}
