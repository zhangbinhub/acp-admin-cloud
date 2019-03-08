package pers.acp.admin.oauth.token;

import org.springframework.security.oauth2.provider.token.TokenStore;
import pers.acp.admin.oauth.vo.LoginLogVO;

import java.util.List;

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
public interface SecurityTokenStore extends TokenStore {

    /**
     * 记录获取token的次数
     *
     * @param appId  应用id
     * @param userId 用户id
     */
    void storeLoginNum(String appId, String userId) throws Exception;

    /**
     * 获取token的次数
     *
     * @param appId 应用id
     * @return 次数
     */
    List<LoginLogVO> getLoginNum(String appId) throws Exception;

}
