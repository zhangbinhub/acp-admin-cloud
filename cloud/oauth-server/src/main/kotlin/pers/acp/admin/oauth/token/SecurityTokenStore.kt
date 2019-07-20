package pers.acp.admin.oauth.token

import org.springframework.security.oauth2.provider.token.TokenStore
import pers.acp.admin.oauth.vo.LoginLogVo

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
interface SecurityTokenStore : TokenStore {

    /**
     * 记录获取token的次数
     *
     * @param appId  应用id
     * @param userId 用户id
     */
    @Throws(Exception::class)
    fun storeLoginNum(appId: String, userId: String)

    /**
     * 获取token的次数
     *
     * @param appId 应用id
     * @return 次数
     */
    @Throws(Exception::class)
    fun getLoginNum(appId: String): MutableList<LoginLogVo>

}
