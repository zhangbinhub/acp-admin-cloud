package pers.acp.admin.common.hystrix

import feign.FeignException
import org.springframework.security.oauth2.common.OAuth2AccessToken
import pers.acp.admin.common.base.BaseFeignHystrix
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.common.vo.ApplicationVo
import pers.acp.admin.common.vo.RuntimeConfigVo
import pers.acp.admin.common.vo.UserVo
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 14/11/2019
 * @since JDK 11
 */
class CommonOauthServerHystrix
constructor(logAdapter: LogAdapter) : BaseFeignHystrix<CommonOauthServer>(logAdapter) {
    override fun create(cause: Throwable?): CommonOauthServer {
        if (cause is FeignException.Unauthorized || cause is FeignException.Forbidden) {
            logAdapter.error("token无效")
            return object : CommonOauthServer {
                @Throws(ServerException::class)
                override fun appInfo(token: String): ApplicationVo {
                    val errMsg = "该token找不到对应的应用信息【$token】"
                    logAdapter.info(errMsg)
                    return ApplicationVo()
                }

                @Throws(ServerException::class)
                override fun tokenInfo(token: String): OAuth2AccessToken? {
                    val errMsg = "该token找不到对应的用户信息【$token】"
                    logAdapter.info(errMsg)
                    return null
                }

                @Throws(ServerException::class)
                override fun userInfo(token: String): UserVo? {
                    val errMsg = "该token找不到对应的用户详细信息【$token】"
                    logAdapter.info(errMsg)
                    return null
                }

                @Throws(ServerException::class)
                override fun findRuntimeConfigByName(name: String): RuntimeConfigVo {
                    logAdapter.error("获取运行参数失败 【$name】")
                    return RuntimeConfigVo()
                }
            }
        } else {
            val errMsg = "调用 oauth2-server 异常: " + cause?.message
            logAdapter.error(errMsg, cause)
            throw ServerException(errMsg)
        }
    }
}