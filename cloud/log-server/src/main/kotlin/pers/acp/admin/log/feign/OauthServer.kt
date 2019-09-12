package pers.acp.admin.log.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import pers.acp.admin.log.hystrix.OauthServerHystrix
import pers.acp.admin.log.vo.ApplicationVo
import pers.acp.admin.log.vo.UserVo
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
     * note: 所有服务
     */
    @RequestMapping(value = ["/inner/application"], method = [RequestMethod.GET])
    @Throws(ServerException::class)
    fun appInfo(@RequestParam(name = "access_token") token: String): ApplicationVo

    /**
     * 获取应用配置信息
     * note: 所有服务
     */
    @RequestMapping(value = ["/inner/userinfo"], method = [RequestMethod.GET])
    @Throws(ServerException::class)
    fun userInfo(@RequestParam(name = "access_token") token: String): UserVo

}