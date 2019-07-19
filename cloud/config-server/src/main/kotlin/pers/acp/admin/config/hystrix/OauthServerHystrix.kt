package pers.acp.admin.config.hystrix

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.admin.common.base.BaseFeignHystrix
import pers.acp.admin.config.feign.OauthServer
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.cloud.log.LogInstance

/**
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
@Component
class OauthServerHystrix @Autowired
constructor(logInstance: LogInstance, objectMapper: ObjectMapper) : BaseFeignHystrix<OauthServer>(logInstance, objectMapper) {

    override fun create(cause: Throwable): OauthServer {
        logInstance.error("调用 oauth2-server 异常: " + cause.message, cause)
        return object : OauthServer {
            @Throws(ServerException::class)
            override fun busRefresh() {
                val errMsg = "配置信息刷新失败"
                logInstance.info(errMsg)
                throw ServerException(errMsg)
            }

            @Throws(ServerException::class)
            override fun busRefresh(application: String) {
                val errMsg = "配置信息刷新失败: application.name = $application"
                logInstance.info(errMsg)
                throw ServerException(errMsg)
            }

            @Throws(ServerException::class)
            override fun busRefreshMatcher(matcher: String) {
                val errMsg = "配置信息刷新失败: matcher = $matcher"
                logInstance.info(errMsg)
                throw ServerException(errMsg)
            }
        }
    }

}
