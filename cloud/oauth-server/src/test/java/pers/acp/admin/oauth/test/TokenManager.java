package pers.acp.admin.oauth.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import pers.acp.admin.oauth.BaseTest;
import pers.acp.admin.oauth.token.store.SecurityTokenStoreRedis;
import pers.acp.spring.boot.interfaces.LogAdapter;

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
class TokenManager extends BaseTest {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LogAdapter logAdapter;

    @Test
    void removeToken() {
        String token = "efe5e360-2bea-4881-9581-35a8b23fa65b";
        String refreshToken = "";
        SecurityTokenStoreRedis redisTokenStore = new SecurityTokenStoreRedis(logAdapter, redisTemplate, objectMapper);
        redisTokenStore.removeAccessToken(token);
        redisTokenStore.removeRefreshToken(refreshToken);
    }

}
