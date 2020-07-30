package pers.acp.admin.oauth.token

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.token.store.SecurityTokenStoreMemory
import pers.acp.admin.oauth.token.store.SecurityTokenStoreRedis
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class SecurityTokenService @Autowired
constructor(private val logAdapter: LogAdapter,
            private val redisTemplate: RedisTemplate<Any, Any>,
            val securityTokenEnhancer: SecurityTokenEnhancer) : DefaultTokenServices() {

    /**
     * 默认持久化到内存
     */
    private var customerTokenStore: TokenStore = inMemoryTokenStore()

    fun getTokenStore(): TokenStore = this.customerTokenStore

    private fun redisTokenStore(): TokenStore = SecurityTokenStoreRedis(logAdapter, redisTemplate)

    private fun inMemoryTokenStore(): TokenStore = SecurityTokenStoreMemory()

    init {
        try {
            Class.forName("org.springframework.data.redis.connection.RedisConnection")?.also {
                // 持久化到 Redis
                this.customerTokenStore = redisTokenStore()
            }
        } catch (e: Throwable) {
        }
        setCustomerObj()
    }

    private final fun setCustomerObj() {
        setTokenStore(customerTokenStore)
        setTokenEnhancer(securityTokenEnhancer)
    }

    @Transactional
    @Throws(AuthenticationException::class)
    override fun createAccessToken(authentication: OAuth2Authentication): OAuth2AccessToken? {
        removeToken(customerTokenStore.getAccessToken(authentication))
        return super.createAccessToken(authentication)
    }

    /**
     * 获取token详细信息
     *
     * @param authentication 授权信息
     * @return token对象
     */
    @Throws(ServerException::class)
    fun getToken(authentication: OAuth2Authentication): OAuth2AccessToken = customerTokenStore.getAccessToken(authentication)
            ?: throw ServerException("access token was not find")

    /**
     * 获取token详细信息
     *
     * @param tokenValue token值
     * @return token对象
     */
    @Throws(ServerException::class)
    fun getToken(tokenValue: String): OAuth2AccessToken = customerTokenStore.readAccessToken(tokenValue)
            ?: throw ServerException("access token was not find")

    /**
     * 根据应用id和登录账号获取token
     *
     * @param appId   应用id
     * @param loginNo 登录账号
     * @return token 集合
     */
    fun getTokensByAppIdAndLoginNo(appId: String, loginNo: String): Collection<OAuth2AccessToken> = customerTokenStore.findTokensByClientIdAndUserName(appId, loginNo)

    /**
     * 根据应用id获取token
     *
     * @param appId 应用id
     * @return token 集合
     */
    fun getTokensByAppId(appId: String): Collection<OAuth2AccessToken> = customerTokenStore.findTokensByClientId(appId)

    @Transactional
    fun removeToken(user: OAuth2Authentication) = removeToken(customerTokenStore.getAccessToken(user))

    /**
     * 删除token
     *
     * @param oAuth2AccessToken token
     */
    @Transactional
    fun removeToken(oAuth2AccessToken: OAuth2AccessToken?) {
        oAuth2AccessToken?.let { token ->
            val refreshToken = token.refreshToken
            refreshToken?.let {
                customerTokenStore.removeRefreshToken(it)
            }
            customerTokenStore.removeAccessToken(oAuth2AccessToken)
        }
    }

    /**
     * 删除指定token
     *
     * @param appId   应用id
     * @param loginNo 登录账号
     */
    @Transactional
    fun removeTokensByAppIdAndLoginNo(appId: String, loginNo: String) = getTokensByAppIdAndLoginNo(appId, loginNo).forEach { this.removeToken(it) }

}
