package pers.acp.admin.oauth.token

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.constant.TokenConstant
import pers.acp.admin.oauth.token.store.SecurityTokenStoreRedis
import pers.acp.admin.oauth.vo.LoginLogVo
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.cloud.log.LogInstance

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class SecurityTokenService @Autowired
constructor(private val logInstance: LogInstance,
            private val redisTemplate: RedisTemplate<Any, Any>,
            val securityTokenEnhancer: SecurityTokenEnhancer,
            private val objectMapper: ObjectMapper) : DefaultTokenServices() {

    private val tokenStore: SecurityTokenStore

    fun getTokenStore(): TokenStore = this.tokenStore

    private fun redisTokenStore(): SecurityTokenStore = SecurityTokenStoreRedis(redisTemplate, objectMapper)

    private fun inMemoryTokenStore(): TokenStore = InMemoryTokenStore()

    init {
        // 持久化到内存
        //        this.tokenStore = inMemoryTokenStore();
        // 持久化到 Redis
        this.tokenStore = redisTokenStore()
        setCustomerObj()
    }

    private final fun setCustomerObj() {
        setTokenStore(tokenStore)
        setTokenEnhancer(securityTokenEnhancer)
    }

    @Transactional
    @Throws(AuthenticationException::class)
    override fun createAccessToken(authentication: OAuth2Authentication): OAuth2AccessToken? {
        removeToken(tokenStore.getAccessToken(authentication))
        val oAuth2AccessToken = super.createAccessToken(authentication)
        try {
            tokenStore.storeLoginNum(authentication.oAuth2Request.clientId, oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_ID].toString())
        } catch (e: Exception) {
            logInstance.error(e.message, e)
        }
        return oAuth2AccessToken
    }

    /**
     * 获取token详细信息
     *
     * @param authentication 授权信息
     * @return token对象
     */
    fun getToken(authentication: OAuth2Authentication): OAuth2AccessToken = tokenStore.getAccessToken(authentication)

    /**
     * 获取token详细信息
     *
     * @param tokenValue token值
     * @return token对象
     */
    fun getToken(tokenValue: String): OAuth2AccessToken = tokenStore.readAccessToken(tokenValue)

    /**
     * 根据应用id和登录账号获取token
     *
     * @param appId   应用id
     * @param loginNo 登录账号
     * @return token 集合
     */
    fun getTokensByAppIdAndLoginNo(appId: String, loginNo: String): Collection<OAuth2AccessToken> = tokenStore.findTokensByClientIdAndUserName(appId, loginNo)

    /**
     * 根据应用id获取token
     *
     * @param appId 应用id
     * @return token 集合
     */
    fun getTokensByAppId(appId: String): Collection<OAuth2AccessToken> = tokenStore.findTokensByClientId(appId)

    @Transactional
    fun removeToken(user: OAuth2Authentication) = removeToken(tokenStore.getAccessToken(user))

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
                tokenStore.removeRefreshToken(it)
            }
            tokenStore.removeAccessToken(oAuth2AccessToken)
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

    @Throws(ServerException::class)
    fun getLoginLogList(appId: String): List<LoginLogVo> =
            try {
                tokenStore.getLoginNum(appId)
            } catch (e: Exception) {
                throw ServerException(e.message)
            }

}
