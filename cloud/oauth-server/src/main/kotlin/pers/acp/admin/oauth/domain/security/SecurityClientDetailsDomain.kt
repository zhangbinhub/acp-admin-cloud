package pers.acp.admin.oauth.domain.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.oauth.repo.ApplicationRepository
import pers.acp.spring.boot.interfaces.LogAdapter

import javax.annotation.PostConstruct

/**
 * @author zhangbin by 11/04/2018 15:21
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class SecurityClientDetailsDomain @Autowired
constructor(private val logAdapter: LogAdapter, private val applicationRepository: ApplicationRepository) : ClientDetailsService {

    private var clientDetailsService: ClientDetailsService? = null

    /**
     * 初始化客户端信息
     */
    @PostConstruct
    fun loadClientInfo() {
        val applicationList = applicationRepository.findAll()
        val memoryClientDetailsServiceBuilder = InMemoryClientDetailsServiceBuilder()
        var builder = memoryClientDetailsServiceBuilder.withClient("test")
                .secret("test")
                .authorizedGrantTypes("password", "client_credentials", "refresh_token")
                .authorities(RoleCode.prefix + RoleCode.SUPER)
                .scopes("test")
                .accessTokenValiditySeconds(86400)
                .refreshTokenValiditySeconds(2592000)
        applicationList.forEach { application ->
            builder = builder.and()
                    .withClient(application.id)
                    .secret(application.secret)
                    .authorizedGrantTypes("password", "client_credentials", "refresh_token")
                    .scopes("ALL")
                    .accessTokenValiditySeconds(application.accessTokenValiditySeconds)
                    .refreshTokenValiditySeconds(application.refreshTokenValiditySeconds)
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
