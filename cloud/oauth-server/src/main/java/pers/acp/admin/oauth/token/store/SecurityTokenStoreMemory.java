package pers.acp.admin.oauth.token.store;

import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import pers.acp.admin.common.constant.CommonConstant;
import pers.acp.admin.oauth.token.LoginLog;
import pers.acp.admin.oauth.token.SecurityTokenStore;
import pers.acp.admin.oauth.vo.LoginLogVO;
import pers.acp.core.CommonTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
public class SecurityTokenStoreMemory extends InMemoryTokenStore implements SecurityTokenStore {

    private static ConcurrentLinkedQueue<LoginLog> loginLogConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void storeLoginNum(String appId, String userId) {
        LoginLog loginLog = new LoginLog();
        loginLog.setAppid(appId);
        loginLog.setDate(CommonTools.getDateTimeString(null, CommonConstant.DATE_FORMAT));
        loginLog.setUserid(userId);
        loginLogConcurrentLinkedQueue.add(loginLog);
    }

    @Override
    public List<LoginLogVO> getLoginNum(String appId) throws Exception {
        List<LoginLogVO> loginLogVOList = new ArrayList<>();
        loginLogConcurrentLinkedQueue.forEach(loginLog -> SecurityTokenStoreUtil.parseLoginLogVO(loginLogVOList, loginLog));
        return loginLogVOList;
    }

}
