package pers.acp.admin.oauth.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.constant.TokenConstant;
import pers.acp.admin.oauth.token.store.SecurityTokenStoreRedis;
import pers.acp.admin.oauth.vo.LoginLogVO;
import pers.acp.springboot.core.exceptions.ServerException;

import java.util.Collection;
import java.util.List;

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class SecurityTokenService extends DefaultTokenServices {

    public TokenStore getTokenStore() {
        return this.tokenStore;
    }

    public SecurityTokenEnhancer getSecurityTokenEnhancer() {
        return securityTokenEnhancer;
    }

    private SecurityTokenStore redisTokenStore() {
        return new SecurityTokenStoreRedis(connectionFactory, objectMapper);
    }

    private TokenStore inMemoryTokenStore() {
        return new InMemoryTokenStore();
    }

    private final RedisConnectionFactory connectionFactory;

    private final SecurityTokenStore tokenStore;

    private final SecurityTokenEnhancer securityTokenEnhancer;

    private final ObjectMapper objectMapper;

    @Autowired
    public SecurityTokenService(RedisConnectionFactory connectionFactory, SecurityTokenEnhancer securityTokenEnhancer, ObjectMapper objectMapper) {
        this.connectionFactory = connectionFactory;
        this.securityTokenEnhancer = securityTokenEnhancer;
        this.objectMapper = objectMapper;
        // 持久化到内存
//        this.tokenStore = inMemoryTokenStore();
        // 持久化到 Redis
        this.tokenStore = redisTokenStore();
        setTokenStore(tokenStore);
        setTokenEnhancer(securityTokenEnhancer);
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
        OAuth2AccessToken oAuth2AccessToken = super.createAccessToken(authentication);
        try {
            tokenStore.storeLoginNum(authentication.getOAuth2Request().getClientId(), oAuth2AccessToken.getAdditionalInformation().get(TokenConstant.USER_INFO_ID).toString());
        } catch (Exception e) {

        }
        return oAuth2AccessToken;
    }

    /**
     * 获取token详细信息
     *
     * @param authentication 授权信息
     * @return token对象
     */
    public OAuth2AccessToken getToken(OAuth2Authentication authentication) {
        return tokenStore.getAccessToken(authentication);
    }

    /**
     * 获取token详细信息
     *
     * @param tokenValue token值
     * @return token对象
     */
    public OAuth2AccessToken getToken(String tokenValue) {
        return tokenStore.readAccessToken(tokenValue);
    }

    /**
     * 根据应用id和登录账号获取token
     *
     * @param appId   应用id
     * @param loginNo 登录账号
     * @return token 集合
     */
    public Collection<OAuth2AccessToken> getTokensByAppIdAndLoginNo(String appId, String loginNo) {
        return tokenStore.findTokensByClientIdAndUserName(appId, loginNo);
    }

    /**
     * 根据应用id获取token
     *
     * @param appId 应用id
     * @return token 集合
     */
    public Collection<OAuth2AccessToken> getTokensByAppId(String appId) {
        return tokenStore.findTokensByClientId(appId);
    }

    /**
     * 删除指定token
     *
     * @param appId   应用id
     * @param loginNo 登录账号
     */
    public void removeTokensByAppIdAndLoginNo(String appId, String loginNo) {
        Collection<OAuth2AccessToken> tokenCollection = getTokensByAppIdAndLoginNo(appId, loginNo);
        tokenCollection.forEach(accessToken -> {
            OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
            if (refreshToken != null) {
                tokenStore.removeRefreshToken(refreshToken);
            }
            tokenStore.removeAccessToken(accessToken);
        });
    }

    public List<LoginLogVO> getLoginLogList(String appId) throws ServerException {
        try {
            return tokenStore.getLoginNum(appId);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

}
