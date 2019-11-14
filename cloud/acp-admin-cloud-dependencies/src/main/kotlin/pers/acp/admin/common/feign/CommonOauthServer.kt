package pers.acp.admin.common.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthOpenInnerApi
import pers.acp.admin.common.hystrix.CommonOauthServerHystrix
import pers.acp.admin.common.vo.RuntimeConfigVo
import java.rmi.ServerException

/**
 * @author zhang by 14/11/2019
 * @since JDK 11
 */
@Component
@FeignClient(value = "oauth2-server", fallbackFactory = CommonOauthServerHystrix::class)
interface CommonOauthServer {

    /**
     * 获取运行参数
     * 参数为空或调用异常均返回默认值的 RuntimeConfigVo 对象
     */
    @RequestMapping(value = [CommonPath.openInnerBasePath + OauthOpenInnerApi.runtimeConfig + "/{name}"], method = [RequestMethod.GET])
    @Throws(ServerException::class)
    fun findRuntimeConfigByName(@PathVariable name: String): RuntimeConfigVo

}