package pers.acp.admin.common.hystrix

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.admin.common.base.BaseFeignHystrix
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.common.vo.RuntimeConfigVo
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 14/11/2019
 * @since JDK 11
 */
@Component
class CommonOauthServerHystrix @Autowired
constructor(logAdapter: LogAdapter, objectMapper: ObjectMapper) : BaseFeignHystrix<CommonOauthServer>(logAdapter, objectMapper) {
    override fun create(cause: Throwable?): CommonOauthServer {
        return object : CommonOauthServer {
            override fun findRuntimeConfigByName(name: String): RuntimeConfigVo {
                logAdapter.error("获取运行参数失败 【$name】")
                return RuntimeConfigVo()
            }
        }
    }
}