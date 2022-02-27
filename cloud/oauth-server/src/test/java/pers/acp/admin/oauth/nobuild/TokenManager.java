package pers.acp.admin.oauth.nobuild;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import pers.acp.admin.oauth.BaseTest;

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
class TokenManager extends BaseTest {

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Test
    void removeToken() {
        String token = "efe5e360-2bea-4881-9581-35a8b23fa65b";
        String refreshToken = "";
        RedisTokenStore redisTokenStore = new RedisTokenStore(connectionFactory);
        redisTokenStore.removeAccessToken(token);
        redisTokenStore.removeRefreshToken(refreshToken);
    }
}
