package pers.acp.admin.oauth.token.store;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import pers.acp.admin.oauth.token.LoginLog;
import pers.acp.admin.oauth.token.SecurityTokenStore;
import pers.acp.admin.oauth.vo.LoginLogVO;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.core.task.timer.Calculation;

import java.util.*;

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
public class SecurityTokenStoreRedis implements TokenStore, SecurityTokenStore {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final RedisTemplate<Object, Object> redisTemplate;

    private static final String ACCESS = "access:";
    private static final String AUTH_TO_ACCESS = "auth_to_access:";
    private static final String AUTH = "auth:";
    private static final String REFRESH_AUTH = "refresh_auth:";
    private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
    private static final String REFRESH = "refresh:";
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
    private static final String CLIENT_ID_TO_ACCESS = "client_id_to_access:";
    private static final String UNAME_TO_ACCESS = "uname_to_access:";

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    private String prefix = "";

    private final ObjectMapper objectMapper;

    private String loginLogPrefix = "login_";

    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
        this.serializationStrategy = serializationStrategy;
    }

    public SecurityTokenStoreRedis(RedisTemplate<Object, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    private byte[] serializeKey(String object) {
        return serialize(prefix + object);
    }

    private <T> T unSerialize(byte[] value, Class<T> cls) {
        return serializationStrategy.deserialize(value, cls);
    }

    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? ""
                : authentication.getUserAuthentication().getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }

    private OAuth2AccessToken deserializeAccessToken(byte[] bytes) {
        return unSerialize(bytes, OAuth2AccessToken.class);
    }

    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return unSerialize(bytes, OAuth2Authentication.class);
    }

    private OAuth2RefreshToken deserializeRefreshToken(byte[] bytes) {
        return unSerialize(bytes, OAuth2RefreshToken.class);
    }

    /**
     * 记录获取token的次数
     *
     * @param appId 应用id
     */
    @Override
    public void storeLoginNum(String appId, String userId) {
        LoginLog loginLog = new LoginLog();
        loginLog.setAppid(appId);
        loginLog.setDate(CommonTools.getDateTimeString(null, Calculation.DATE_FORMAT));
        loginLog.setUserid(userId);
        try {
            redisTemplate.opsForList().rightPush(loginLogPrefix + appId, objectMapper.writeValueAsString(loginLog));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<LoginLogVO> getLoginNum(String appId) throws Exception {
        List<LoginLogVO> loginLogVOList = new ArrayList<>();
        List<Object> loginInfoList = redisTemplate.opsForList().range(loginLogPrefix + appId, 0, -1);
        if (loginInfoList != null) {
            for (Object loginInfo : loginInfoList) {
                SecurityTokenStoreUtil.parseLoginLogVO(loginLogVOList, objectMapper.readValue((String) loginInfo, LoginLog.class));
            }
        }
        return loginLogVOList;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        byte[] tokenInfo = (byte[]) redisTemplate.execute((RedisCallback<Object>) connection -> connection.get(serializeKey(AUTH + token)));
        OAuth2Authentication auth = null;
        try {
            auth = deserializeAuthentication(tokenInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return auth;
    }

    private void expireToken(RedisConnection connection, byte[] refreshToAccessKey, byte[] accessToRefreshKey, ExpiringOAuth2RefreshToken refreshToken) {
        Date expiration = refreshToken.getExpiration();
        if (expiration != null) {
            int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L).intValue();
            connection.expire(refreshToAccessKey, seconds);
            connection.expire(accessToRefreshKey, seconds);
        }
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        try {
            byte[] serializedAccessToken = serialize(token);
            byte[] serializedAuth = serialize(authentication);
            byte[] accessKey = serializeKey(ACCESS + token.getValue());
            byte[] authKey = serializeKey(AUTH + token.getValue());
            byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication));
            byte[] approvalKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(authentication));
            byte[] clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                try {
                    connection.set(accessKey, serializedAccessToken);
                    connection.set(authKey, serializedAuth);
                    connection.set(authToAccessKey, serializedAccessToken);
                    if (!authentication.isClientOnly()) {
                        connection.rPush(approvalKey, serializedAccessToken);
                    }
                    connection.rPush(clientId, serializedAccessToken);
                    if (token.getExpiration() != null) {
                        int seconds = token.getExpiresIn();
                        connection.expire(accessKey, seconds);
                        connection.expire(authKey, seconds);
                        connection.expire(authToAccessKey, seconds);
                        connection.expire(clientId, seconds);
                        connection.expire(approvalKey, seconds);
                    }
                    OAuth2RefreshToken refreshToken = token.getRefreshToken();
                    if (refreshToken != null && refreshToken.getValue() != null) {

                        byte[] refresh = serialize(token.getRefreshToken().getValue());
                        byte[] auth = serialize(token.getValue());
                        byte[] refreshToAccessKey = serializeKey(REFRESH_TO_ACCESS + token.getRefreshToken().getValue());
                        byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + token.getValue());
                        connection.set(refreshToAccessKey, auth);
                        connection.set(accessToRefreshKey, refresh);
                        if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                            expireToken(connection, refreshToAccessKey, accessToRefreshKey, (ExpiringOAuth2RefreshToken) refreshToken);
                        }
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private OAuth2AccessToken readAccessTokenByKey(byte[] key) {
        return deserializeAccessToken((byte[]) redisTemplate.execute((RedisCallback<Object>) connection -> connection.get(key)));
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        byte[] key = serializeKey(ACCESS + tokenValue);
        return readAccessTokenByKey(key);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        removeAccessToken(token.getValue());
    }

    public void removeAccessToken(String tokenValue) {
        byte[] accessKey = serializeKey(ACCESS + tokenValue);
        byte[] authKey = serializeKey(AUTH + tokenValue);
        byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            try {
                connection.get(accessKey);
                connection.get(authKey);
                connection.del(accessKey);
                connection.del(accessToRefreshKey);
                // Don't remove the refresh token - it's up to the caller to do that
                connection.del(authKey);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            return null;
        });
        Object access = results.get(0);
        Object auth = results.get(1);
        OAuth2Authentication authentication = (OAuth2Authentication) auth;
        if (authentication != null) {
            String key = authenticationKeyGenerator.extractKey(authentication);
            byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + key);
            byte[] unameKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(authentication));
            byte[] clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                try {
                    connection.del(authToAccessKey);
                    connection.lRem(unameKey, 1, serialize(access));
                    connection.lRem(clientId, 1, serialize(access));
                    connection.del(serialize(ACCESS + key));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            });
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        try {
            byte[] refreshKey = serializeKey(REFRESH + refreshToken.getValue());
            byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + refreshToken.getValue());
            byte[] serializedRefreshToken = serialize(refreshToken);
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                try {
                    connection.set(refreshKey, serializedRefreshToken);
                    connection.set(refreshAuthKey, serialize(authentication));
                    if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                        expireToken(connection, refreshKey, refreshAuthKey, (ExpiringOAuth2RefreshToken) refreshToken);
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return null;
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        byte[] key = serializeKey(REFRESH + tokenValue);
        return deserializeRefreshToken((byte[]) redisTemplate.execute((RedisCallback<Object>) connection -> connection.get(key)));
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        return deserializeAuthentication((byte[]) redisTemplate.execute((RedisCallback<Object>) connection -> connection.get(serializeKey(REFRESH_AUTH + token))));
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        removeRefreshToken(refreshToken.getValue());
    }

    public void removeRefreshToken(String tokenValue) {
        byte[] refreshKey = serializeKey(REFRESH + tokenValue);
        byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + tokenValue);
        byte[] refresh2AccessKey = serializeKey(REFRESH_TO_ACCESS + tokenValue);
        byte[] access2RefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.del(refreshKey);
            connection.del(refreshAuthKey);
            connection.del(refresh2AccessKey);
            connection.del(access2RefreshKey);
            return null;
        });
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    public void removeAccessTokenUsingRefreshToken(String refreshToken) {
        byte[] key = serializeKey(REFRESH_TO_ACCESS + refreshToken);
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.get(key);
            connection.del(key);
            return null;
        });
        if (results.size() > 0) {
            String accessToken = (String) results.get(0);
            removeAccessToken(accessToken);
        }
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);
        byte[] serializedKey = serializeKey(AUTH_TO_ACCESS + key);
        OAuth2AccessToken accessToken = readAccessTokenByKey(serializedKey);
        if (accessToken != null) {
            OAuth2Authentication storedAuthentication = readAuthentication(accessToken.getValue());
            if ((storedAuthentication == null || !key.equals(authenticationKeyGenerator.extractKey(storedAuthentication)))) {
                storeAccessToken(accessToken, authentication);
            }

        }
        return accessToken;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return findTokenByKey(serializeKey(UNAME_TO_ACCESS + getApprovalKey(clientId, userName)));
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return findTokenByKey(serializeKey(CLIENT_ID_TO_ACCESS + clientId));
    }

    private Collection<OAuth2AccessToken> findTokenByKey(byte[] key) {
        final List[] tokenList = new List[]{new ArrayList<>()};
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            tokenList[0] = connection.lRange(key, 0, -1);
            return null;
        });
        if (tokenList[0] == null || tokenList[0].size() == 0) {
            return Collections.emptySet();
        }
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();
        for (Object token : tokenList[0]) {
            accessTokens.add(deserializeAccessToken((byte[]) token));
        }
        return Collections.unmodifiableCollection(accessTokens);
    }
}
