package pers.acp.admin.common.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.web.bind.annotation.*
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthApi
import pers.acp.admin.common.hystrix.CommonOauthServerHystrix
import pers.acp.admin.common.vo.*
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
    @GetMapping(value = [CommonPath.innerBasePath + OauthApi.appInfo], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun appInfo(@RequestParam(name = "access_token") token: String): ApplicationVo

    /**
     * 获取token详细信息
     */
    @GetMapping(value = [CommonPath.innerBasePath + OauthApi.currToken], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun tokenInfo(@RequestParam(name = "access_token") token: String): OAuth2AccessToken?

    /**
     * 获取token详细信息
     */
    @GetMapping(value = [CommonPath.innerBasePath + OauthApi.currToken], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun tokenInfo(): OAuth2AccessToken?

    /**
     * 当前用户是否具有指定的功能权限
     */
    @GetMapping(value = [CommonPath.innerBasePath + OauthApi.moduleFunc + "/{moduleFuncCode}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun hasModuleFunc(@PathVariable moduleFuncCode: String): BooleanInfoVo

    /**
     * 指定用户是否具有指定的功能权限
     */
    @GetMapping(value = [CommonPath.openInnerBasePath + OauthApi.moduleFunc + "/{userId}/{moduleFuncCode}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun hasModuleFunc(@PathVariable userId: String, @PathVariable moduleFuncCode: String): BooleanInfoVo

    /**
     * 获取用户详细信息
     */
    @GetMapping(value = [CommonPath.innerBasePath + OauthApi.currUser], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun userInfo(@RequestParam(name = "access_token") token: String): UserVo?

    /**
     * 获取用户详细信息
     */
    @GetMapping(value = [CommonPath.innerBasePath + OauthApi.currUser], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun userInfo(): UserVo?

    @GetMapping(value = [CommonPath.openInnerBasePath + OauthApi.userConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findUserById(@RequestParam id: String): UserVo

    @GetMapping(value = [CommonPath.openInnerBasePath + OauthApi.userConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findUserByLoginNo(@RequestParam loginNo: String): UserVo

    @GetMapping(value = [CommonPath.innerBasePath + OauthApi.currModuleFunc], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findModuleFuncByCurrUser(): List<ModuleFuncVo>

    /**
     * 获取用户列表
     */
    @GetMapping(value = [CommonPath.innerBasePath + OauthApi.userList], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findUserListInCurrOrg(@RequestParam roleCode: String): List<UserVo>

    /**
     * 获取用户列表
     */
    @GetMapping(value = [CommonPath.innerBasePath + OauthApi.userList], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findUserListByOrgLevel(@RequestParam orgLevel: String, @RequestParam roleCode: String): List<UserVo>

    /**
     * 获取用户列表
     */
    @GetMapping(value = [CommonPath.openInnerBasePath + OauthApi.userList], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findUserList(@RequestParam orgCode: String, @RequestParam roleCode: String): List<UserVo>

    /**
     * 获取用户列表
     */
    @GetMapping(value = [CommonPath.openInnerBasePath + OauthApi.userList], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findUserList(@RequestParam roleCode: String): List<UserVo>

    /**
     * 获取运行参数
     * 参数为空或调用异常均返回默认值的 RuntimeConfigVo 对象
     */
    @GetMapping(value = [CommonPath.openInnerBasePath + OauthApi.runtime + "/{name}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findRuntimeConfigByName(@PathVariable name: String): RuntimeConfigVo

    /**
     * 获取运行参数
     * 参数为空或调用异常均返回默认值的 RuntimeConfigVo 对象
     */
    @GetMapping(value = [CommonPath.openInnerBasePath + OauthApi.runtime], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun findRuntimeConfigMap(): Map<String, RuntimeConfigVo>

}