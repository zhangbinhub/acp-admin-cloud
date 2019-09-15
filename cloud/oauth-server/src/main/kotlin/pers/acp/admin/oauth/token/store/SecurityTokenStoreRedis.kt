package pers.acp.admin.oauth.token.store

import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2RefreshToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

import java.util.*

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
class SecurityTokenStoreRedis(private val logAdapter: LogAdapter,
                              private val redisTemplate: RedisTemplate<Any, Any>) : TokenStore {

    private var authenticationKeyGenerator: AuthenticationKeyGenerator = DefaultAuthenticationKeyGenerator()

    private var serializationStrategy: RedisTokenStoreSerializationStrategy = JdkSerializationStrategy()

    private val prefix = ""

    fun setAuthenticationKeyGenerator(authenticationKeyGenerator: AuthenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator
    }

    fun setSerializationStrategy(serializationStrategy: RedisTokenStoreSerializationStrategy) {
        this.serializationStrategy = serializationStrategy
    }

    private fun serialize(`object`: Any): ByteArray {
        return serializationStrategy.serialize(`object`)
    }

    private fun serializeKey(`object`: String): ByteArray {
        return serialize(prefix + `object`)
    }

    private fun <T> unSerialize(value: ByteArray, cls: Class<T>): T {
        return serializationStrategy.deserialize(value, cls)
    }

    private fun deserializeAccessToken(bytes: ByteArray?): OAuth2AccessToken? {
        if (bytes == null) return null
        return unSerialize(bytes, OAuth2AccessToken::class.java)
    }

    private fun deserializeAuthentication(bytes: ByteArray?): OAuth2Authentication? {
        if (bytes == null) return null
        return unSerialize(bytes, OAuth2Authentication::class.java)
    }

    private fun deserializeRefreshToken(bytes: ByteArray?): OAuth2RefreshToken? {
        if (bytes == null) return null
        return unSerialize(bytes, OAuth2RefreshToken::class.java)
    }

    override fun readAuthentication(token: OAuth2AccessToken): OAuth2Authentication? {
        return readAuthentication(token.value)
    }

    override fun readAuthentication(token: String): OAuth2Authentication? {
        val tokenInfo = redisTemplate.execute { connection -> connection.get(serializeKey(AUTH + token)) }
        var auth: OAuth2Authentication? = null
        try {
            auth = deserializeAuthentication(tokenInfo)
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }

        return auth
    }

    private fun expireToken(connection: RedisConnection, refreshToAccessKey: ByteArray, accessToRefreshKey: ByteArray, refreshToken: ExpiringOAuth2RefreshToken) {
        refreshToken.expiration?.let {
            val seconds = (it.time - System.currentTimeMillis()) / 1000L
            connection.expire(refreshToAccessKey, seconds)
            connection.expire(accessToRefreshKey, seconds)
        }
    }

    override fun storeAccessToken(token: OAuth2AccessToken, authentication: OAuth2Authentication) {
        try {
            val serializedAccessToken = serialize(token)
            val serializedAuth = serialize(authentication)
            val accessKey = serializeKey(ACCESS + token.value)
            val authKey = serializeKey(AUTH + token.value)
            val authToAccessKey = serializeKey(AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication))
            val approvalKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(authentication))
            val clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.oAuth2Request.clientId)
            redisTemplate.executePipelined { connection ->
                try {
                    connection.set(accessKey, serializedAccessToken)
                    connection.set(authKey, serializedAuth)
                    connection.set(authToAccessKey, serializedAccessToken)
                    if (!authentication.isClientOnly) {
                        connection.rPush(approvalKey, serializedAccessToken)
                    }
                    connection.rPush(clientId, serializedAccessToken)
                    if (token.expiration != null) {
                        val seconds = token.expiresIn
                        connection.expire(accessKey, seconds.toLong())
                        connection.expire(authKey, seconds.toLong())
                        connection.expire(authToAccessKey, seconds.toLong())
                        connection.expire(clientId, seconds.toLong())
                        connection.expire(approvalKey, seconds.toLong())
                    }
                    val refreshToken = token.refreshToken
                    if (refreshToken != null && refreshToken.value != null) {
                        val refresh = serialize(token.refreshToken.value)
                        val auth = serialize(token.value)
                        val refreshToAccessKey = serializeKey(REFRESH_TO_ACCESS + token.refreshToken.value)
                        val accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + token.value)
                        connection.set(refreshToAccessKey, auth)
                        connection.set(accessToRefreshKey, refresh)
                        if (refreshToken is ExpiringOAuth2RefreshToken) {
                            expireToken(connection, refreshToAccessKey, accessToRefreshKey, refreshToken)
                        }
                    }
                } catch (ex: Exception) {
                    throw RuntimeException(ex)
                }
                null
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }

    }

    private fun readAccessTokenByKey(key: ByteArray): OAuth2AccessToken? {
        return deserializeAccessToken(redisTemplate.execute { connection -> connection.get(key) })
    }

    override fun readAccessToken(tokenValue: String): OAuth2AccessToken? {
        val key = serializeKey(ACCESS + tokenValue)
        return readAccessTokenByKey(key)
    }

    override fun removeAccessToken(token: OAuth2AccessToken) {
        removeAccessToken(token.value)
    }

    fun removeAccessToken(tokenValue: String) {
        val accessKey = serializeKey(ACCESS + tokenValue)
        val authKey = serializeKey(AUTH + tokenValue)
        val accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue)
        redisTemplate.execute { connection ->
            try {
                val access = connection.get(accessKey)
                val auth = connection.get(authKey)
                connection.del(accessKey)
                connection.del(accessToRefreshKey)
                // Don't remove the refresh token - it's up to the caller to do that
                connection.del(authKey)
                deserializeAuthentication(auth)?.apply {
                    val key = authenticationKeyGenerator.extractKey(this)
                    val authToAccessKey = serializeKey(AUTH_TO_ACCESS + key)
                    val uNameKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(this))
                    val clientId = serializeKey(CLIENT_ID_TO_ACCESS + this.oAuth2Request.clientId)
                    connection.del(authToAccessKey)
                    connection.del(serialize(ACCESS + key))
                    access?.let {
                        connection.lRem(uNameKey, 1, it)
                        connection.lRem(clientId, 1, it)
                    }
                }
            } catch (ex: Exception) {
                throw RuntimeException(ex)
            }
            null
        }
    }

    override fun storeRefreshToken(refreshToken: OAuth2RefreshToken, authentication: OAuth2Authentication) {
        try {
            val refreshKey = serializeKey(REFRESH + refreshToken.value)
            val refreshAuthKey = serializeKey(REFRESH_AUTH + refreshToken.value)
            val serializedRefreshToken = serialize(refreshToken)
            redisTemplate.executePipelined { connection ->
                try {
                    connection.set(refreshKey, serializedRefreshToken)
                    connection.set(refreshAuthKey, serialize(authentication))
                    if (refreshToken is ExpiringOAuth2RefreshToken) {
                        expireToken(connection, refreshKey, refreshAuthKey, refreshToken)
                    }
                } catch (ex: Exception) {
                    throw RuntimeException(ex)
                }
                null
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }

    }

    override fun readRefreshToken(tokenValue: String): OAuth2RefreshToken? {
        val key = serializeKey(REFRESH + tokenValue)
        return deserializeRefreshToken(redisTemplate.execute { connection -> connection.get(key) })
    }

    override fun readAuthenticationForRefreshToken(token: OAuth2RefreshToken): OAuth2Authentication {
        return readAuthenticationForRefreshToken(token.value)
    }

    fun readAuthenticationForRefreshToken(token: String): OAuth2Authentication =
            deserializeAuthentication(redisTemplate.execute { connection ->
                connection.get(serializeKey(REFRESH_AUTH + token))
            }) ?: throw ServerException("token not exist")

    override fun removeRefreshToken(refreshToken: OAuth2RefreshToken) {
        removeRefreshToken(refreshToken.value)
    }

    fun removeRefreshToken(tokenValue: String) {
        val refreshKey = serializeKey(REFRESH + tokenValue)
        val refreshAuthKey = serializeKey(REFRESH_AUTH + tokenValue)
        val refresh2AccessKey = serializeKey(REFRESH_TO_ACCESS + tokenValue)
        val access2RefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue)
        redisTemplate.executePipelined { connection ->
            connection.del(refreshKey)
            connection.del(refreshAuthKey)
            connection.del(refresh2AccessKey)
            connection.del(access2RefreshKey)
            null
        }
    }

    override fun removeAccessTokenUsingRefreshToken(refreshToken: OAuth2RefreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.value)
    }

    fun removeAccessTokenUsingRefreshToken(refreshToken: String) {
        val key = serializeKey(REFRESH_TO_ACCESS + refreshToken)
        val results = redisTemplate.executePipelined { connection ->
            connection.get(key)
            connection.del(key)
            null
        }
        if (results.size > 0) {
            val accessToken = results[0] as String
            removeAccessToken(accessToken)
        }
    }

    override fun getAccessToken(authentication: OAuth2Authentication): OAuth2AccessToken? {
        val key = authenticationKeyGenerator.extractKey(authentication)
        val serializedKey = serializeKey(AUTH_TO_ACCESS + key)
        val accessToken = readAccessTokenByKey(serializedKey)
        if (accessToken != null) {
            val storedAuthentication = readAuthentication(accessToken.value)
            if (storedAuthentication == null || key != authenticationKeyGenerator.extractKey(storedAuthentication)) {
                storeAccessToken(accessToken, authentication)
            }

        }
        return accessToken
    }

    override fun findTokensByClientIdAndUserName(clientId: String, userName: String): Collection<OAuth2AccessToken> {
        return findTokenByKey(serializeKey(UNAME_TO_ACCESS + getApprovalKey(clientId, userName)))
    }

    override fun findTokensByClientId(clientId: String): Collection<OAuth2AccessToken> {
        return findTokenByKey(serializeKey(CLIENT_ID_TO_ACCESS + clientId))
    }

    private fun findTokenByKey(key: ByteArray): Collection<OAuth2AccessToken> {
        var tokenList: MutableList<ByteArray>? = null
        redisTemplate.execute { connection ->
            tokenList = connection.lRange(key, 0, -1)
            null
        }
        if (tokenList == null || tokenList!!.isEmpty()) {
            return emptySet()
        }
        val accessTokens: MutableList<OAuth2AccessToken> = mutableListOf()
        tokenList!!.forEach { item -> accessTokens.add(deserializeAccessToken(item)!!) }
        return Collections.unmodifiableCollection(accessTokens)
    }

    companion object {

        private const val ACCESS = "access:"
        private const val AUTH_TO_ACCESS = "auth_to_access:"
        private const val AUTH = "auth:"
        private const val REFRESH_AUTH = "refresh_auth:"
        private const val ACCESS_TO_REFRESH = "access_to_refresh:"
        private const val REFRESH = "refresh:"
        private const val REFRESH_TO_ACCESS = "refresh_to_access:"
        private const val CLIENT_ID_TO_ACCESS = "client_id_to_access:"
        private const val UNAME_TO_ACCESS = "uname_to_access:"

        private fun getApprovalKey(authentication: OAuth2Authentication): String {
            val userName = if (authentication.userAuthentication == null)
                ""
            else
                authentication.userAuthentication.name
            return getApprovalKey(authentication.oAuth2Request.clientId, userName)
        }

        private fun getApprovalKey(clientId: String, userName: String?) =
                clientId + if (userName == null) "" else ":$userName"
    }
}
