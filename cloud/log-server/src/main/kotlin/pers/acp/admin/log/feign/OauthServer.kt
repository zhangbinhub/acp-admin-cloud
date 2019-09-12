package pers.acp.admin.log.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthInnerApi
import pers.acp.admin.log.hystrix.OauthServerHystrix
import pers.acp.admin.log.vo.ApplicationVo
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 12/09/2019
 * @since JDK 11
 */
@Component
@FeignClient(value = "oauth2-server", fallbackFactory = OauthServerHystrix::class)
interface OauthServer {

    /**
     * 获取应用配置信息
     */
    @RequestMapping(value = [CommonPath.innerBasePath + OauthInnerApi.appInfo], method = [RequestMethod.GET])
    @Throws(ServerException::class)
    fun appInfo(@RequestParam(name = "access_token") token: String): ApplicationVo

    /**
     * 获取token详细信息
     */
    @RequestMapping(value = [CommonPath.innerBasePath + OauthInnerApi.currToken], method = [RequestMethod.GET])
    @Throws(ServerException::class)
    fun tokenInfo(@RequestParam(name = "access_token") token: String): OAuth2AccessToken?

}