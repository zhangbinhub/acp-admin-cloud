package pers.acp.admin.common.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthInnerApi
import pers.acp.admin.api.OauthOpenInnerApi
import pers.acp.admin.common.hystrix.CommonOauthServerHystrix
import pers.acp.admin.common.vo.ApplicationVo
import pers.acp.admin.common.vo.RuntimeConfigVo
import pers.acp.admin.common.vo.UserVo
import java.rmi.ServerException

/**
 * @author zhang by 14/11/2019
 * @since JDK 11
 */
@FeignClient(value = "oauth2-server", fallbackFactory = CommonOauthServerHystrix::class)
interface CommonOauthServer {

    /**
     * 获取应用配置信息
     */
    @RequestMapping(value = [CommonPath.innerBasePath + OauthInnerApi.appInfo], method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun appInfo(@RequestParam(name = "access_token") token: String): ApplicationVo

    /**
     * 获取token详细信息
     */
    @RequestMapping(value = [CommonPath.innerBasePath + OauthInnerApi.currToken], method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun tokenInfo(@RequestParam(name = "access_token") token: String): OAuth2AccessToken?

    /**
     * 获取用户详细信息
     */
    @RequestMapping(value = [CommonPath.innerBasePath + OauthInnerApi.currUser], method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun userInfo(@RequestParam(name = "access_token") token: String): UserVo?

    /**
     * 获取运行参数
     * 参数为空或调用异常均返回默认值的 RuntimeConfigVo 对象
     */
    @RequestMapping(value = [CommonPath.openInnerBasePath + OauthOpenInnerApi.runtimeConfig + "/{name}"], method = [RequestMethod.GET],
            produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun findRuntimeConfigByName(@PathVariable name: String): RuntimeConfigVo

}