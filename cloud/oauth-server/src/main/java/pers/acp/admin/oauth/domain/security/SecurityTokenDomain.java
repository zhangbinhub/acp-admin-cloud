package pers.acp.admin.oauth.domain.security;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class SecurityTokenDomain extends DefaultTokenServices {

    public TokenStore getTokenStore() {
        return this.tokenStore;
    }

    public SecurityTokenEnhancerDomain getSecurityTokenEnhancerDomain() {
        return securityTokenEnhancerDomain;
    }

    private TokenStore redisTokenStore() {
        return new RedisTokenStore(connectionFactory);
    }

    private TokenStore inMemoryTokenStore() {
        return new InMemoryTokenStore();
    }

    private final RedisConnectionFactory connectionFactory;

    private final TokenStore tokenStore;

    private final SecurityTokenEnhancerDomain securityTokenEnhancerDomain;

    public SecurityTokenDomain(RedisConnectionFactory connectionFactory, SecurityTokenEnhancerDomain securityTokenEnhancerDomain) {
        this.connectionFactory = connectionFactory;
        this.securityTokenEnhancerDomain = securityTokenEnhancerDomain;
        // 持久化到内存
//        this.tokenStore = inMemoryTokenStore();
        // 持久化到 Redis
        this.tokenStore = redisTokenStore();
        setTokenStore(tokenStore);
        setTokenEnhancer(securityTokenEnhancerDomain);
    }

    @Transactional
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);
        if (existingAccessToken != null) {
            OAuth2RefreshToken refreshToken = existingAccessToken.getRefreshToken();
            if (refreshToken != null) {
                tokenStore.removeRefreshToken(refreshToken);
            }
            tokenStore.removeAccessToken(existingAccessToken);
        }
        return super.createAccessToken(authentication);
    }

    /**
     * 根据应用id和登录账号获取token
     *
     * @param appId   应用id
     * @param loginNo 登录账号
     * @return token 集合
     */
    public Collection<OAuth2AccessToken> getTokenByAppIdAndLoginNo(String appId, String loginNo) {
        return tokenStore.findTokensByClientIdAndUserName(appId, loginNo);
    }

    /**
     * 根据应用id获取token
     *
     * @param appId 应用id
     * @return token 集合
     */
    public Collection<OAuth2AccessToken> getTokenByAppId(String appId) {
        return tokenStore.findTokensByClientId(appId);
    }

    /**
     * 删除指定token
     *
     * @param appId   应用id
     * @param loginNo 登录账号
     */
    public void removeTokenByAppIdAndLoginNo(String appId, String loginNo) {
        Collection<OAuth2AccessToken> tokenCollection = getTokenByAppIdAndLoginNo(appId, loginNo);
        tokenCollection.forEach(accessToken -> {
            OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
            if (refreshToken != null) {
                tokenStore.removeRefreshToken(refreshToken);
            }
            tokenStore.removeAccessToken(accessToken);
        });
    }

}
