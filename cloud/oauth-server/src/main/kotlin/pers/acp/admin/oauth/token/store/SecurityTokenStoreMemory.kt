package pers.acp.admin.oauth.token.store

import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore
import pers.acp.admin.oauth.token.LoginLog
import pers.acp.admin.oauth.token.SecurityTokenStore
import pers.acp.admin.oauth.vo.LoginLogVo
import pers.acp.core.CommonTools
import pers.acp.core.task.timer.Calculation

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
class SecurityTokenStoreMemory : InMemoryTokenStore(), SecurityTokenStore {

    @Throws(Exception::class)
    override fun storeLoginNum(appId: String, userId: String) {
        loginLogConcurrentLinkedQueue.add(LoginLog(
                appId = appId,
                date = CommonTools.getDateTimeString(null, Calculation.DATE_FORMAT),
                userId = userId
        ))
    }

    @Throws(Exception::class)
    override fun getLoginNum(appId: String): MutableList<LoginLogVo> {
        val loginLogVoList: MutableList<LoginLogVo> = mutableListOf()
        loginLogConcurrentLinkedQueue.forEach { loginLog -> SecurityTokenStoreUtil.parseLoginLogVO(loginLogVoList, loginLog) }
        return loginLogVoList
    }

    companion object {
        private val loginLogConcurrentLinkedQueue = ConcurrentLinkedQueue<LoginLog>()
    }

}
