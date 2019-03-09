package pers.acp.admin.oauth.token.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import pers.acp.admin.common.constant.CommonConstant;
import pers.acp.admin.oauth.token.LoginLog;
import pers.acp.admin.oauth.token.SecurityTokenStore;
import pers.acp.admin.oauth.vo.LoginLogVO;
import pers.acp.core.CommonTools;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
public class SecurityTokenStoreRedis extends RedisTokenStore implements SecurityTokenStore {

    private final RedisConnectionFactory connectionFactory;

    private final ObjectMapper objectMapper;

    private String loginLogPrefix = "login_";

    public SecurityTokenStoreRedis(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        super(connectionFactory);
        this.connectionFactory = connectionFactory;
        this.objectMapper = objectMapper;
    }

    /**
     * 记录获取token的次数
     *
     * @param appId 应用id
     */
    @Override
    public void storeLoginNum(String appId, String userId) throws Exception {
        LoginLog loginLog = new LoginLog();
        loginLog.setAppid(appId);
        loginLog.setDate(CommonTools.getDateTimeString(null, CommonConstant.DATE_FORMAT));
        loginLog.setUserid(userId);
        String key = loginLogPrefix + appId;
        RedisConnection connection = connectionFactory.getConnection();
        try {
            connection.lPush(key.getBytes(), objectMapper.writeValueAsBytes(loginLog));
        } finally {
            connection.close();
        }
    }

    @Override
    public List<LoginLogVO> getLoginNum(String appId) throws Exception {
        String key = loginLogPrefix + appId;
        List<LoginLogVO> loginLogVOList = new ArrayList<>();
        RedisConnection connection = connectionFactory.getConnection();
        try {
            List<byte[]> loginInfoList = connection.lRange(key.getBytes(), 0, -1);
            if (loginInfoList != null) {
                for (byte[] loginInfo : loginInfoList) {
                    SecurityTokenStoreUtil.parseLoginLogVO(loginLogVOList, objectMapper.readValue(loginInfo, LoginLog.class));
                }
            }
        } finally {
            connection.close();
        }
        return loginLogVOList;
    }

}
