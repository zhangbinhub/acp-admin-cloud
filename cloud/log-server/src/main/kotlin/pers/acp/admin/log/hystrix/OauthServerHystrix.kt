package pers.acp.admin.log.hystrix

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.admin.common.base.BaseFeignHystrix
import pers.acp.admin.log.feign.OauthServer
import pers.acp.admin.log.vo.ApplicationVo
import pers.acp.admin.log.vo.UserVo
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 12/09/2019
 * @since JDK 11
 */
@Component
class OauthServerHystrix @Autowired
constructor(logAdapter: LogAdapter, objectMapper: ObjectMapper) : BaseFeignHystrix<OauthServer>(logAdapter, objectMapper) {
    override fun create(cause: Throwable?): OauthServer {
        logAdapter.error("调用 oauth2-server 异常: " + cause?.message, cause)
        return object : OauthServer {
            @Throws(ServerException::class)
            override fun appInfo(token: String): ApplicationVo {
                val errMsg = "该token找不到对应的应用信息【$token】"
                logAdapter.info(errMsg)
                throw ServerException(errMsg)
            }

            override fun userInfo(token: String): UserVo {
                val errMsg = "该token找不到对应的用户信息【$token】"
                logAdapter.info(errMsg)
                throw ServerException(errMsg)
            }
        }
    }
}