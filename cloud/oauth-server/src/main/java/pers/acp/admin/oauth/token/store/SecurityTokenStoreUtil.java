package pers.acp.admin.oauth.token.store;

import pers.acp.admin.oauth.token.LoginLog;
import pers.acp.admin.oauth.vo.LoginLogVO;

import java.util.List;

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
class SecurityTokenStoreUtil {

    static void parseLoginLogVO(List<LoginLogVO> loginLogVOList, LoginLog loginLog) {
        boolean process = false;
        for (LoginLogVO loginLogVO : loginLogVOList) {
            if (loginLogVO.getAppid().equals(loginLog.getAppid()) && loginLogVO.getDate().equals(loginLog.getDate())) {
                loginLogVO.setCount(loginLogVO.getCount() + 1);
                process = true;
                break;
            }
        }
        if (!process) {
            LoginLogVO loginLogVO = new LoginLogVO();
            loginLogVO.setAppid(loginLog.getAppid());
            loginLogVO.setDate(loginLog.getDate());
            loginLogVO.setCount(1);
            loginLogVOList.add(loginLogVO);
        }
    }

}
