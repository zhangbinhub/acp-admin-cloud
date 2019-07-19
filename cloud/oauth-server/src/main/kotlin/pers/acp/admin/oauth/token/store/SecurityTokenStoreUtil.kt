package pers.acp.admin.oauth.token.store

import pers.acp.admin.oauth.token.LoginLog
import pers.acp.admin.oauth.vo.LoginLogVo

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
internal object SecurityTokenStoreUtil {

    fun parseLoginLogVO(loginLogVoList: MutableList<LoginLogVo>, loginLog: LoginLog) {
        var process = false
        for (loginLogVO in loginLogVoList) {
            if (loginLogVO.appId == loginLog.appId && loginLogVO.date == loginLog.date) {
                loginLogVO.count = loginLogVO.count + 1
                process = true
                break
            }
        }
        if (!process) {
            loginLogVoList.add(LoginLogVo(
                    appId = loginLog.appId,
                    date = loginLog.date,
                    count = 1
            ))
        }
    }

}
