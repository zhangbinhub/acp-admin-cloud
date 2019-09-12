package pers.acp.admin.log.hystrix

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.admin.common.base.BaseFeignHystrix
import pers.acp.admin.log.feign.OauthServer
import pers.acp.admin.log.vo.ApplicationPo
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
        logAdapter.error("获取应用信息异常：" + cause?.message, cause)
        return object : OauthServer {
            @Throws(ServerException::class)
            override fun appInfo(token: String): ApplicationPo {
                val errMsg = "获取应用信息失败"
                logAdapter.info(errMsg)
                throw ServerException(errMsg)
            }
        }
    }
}