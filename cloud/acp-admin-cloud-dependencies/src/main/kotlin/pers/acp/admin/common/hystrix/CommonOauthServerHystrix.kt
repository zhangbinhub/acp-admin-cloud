package pers.acp.admin.common.hystrix

import feign.FeignException
import org.springframework.security.oauth2.common.OAuth2AccessToken
import pers.acp.admin.common.base.BaseFeignHystrix
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.common.vo.*
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
                    logAdapter.error(errMsg)
                    return ApplicationVo()
                }

                @Throws(ServerException::class)
                override fun tokenInfo(token: String): OAuth2AccessToken? {
                    val errMsg = "该token找不到对应的用户信息【$token】"
                    logAdapter.error(errMsg)
                    return null
                }

                @Throws(ServerException::class)
                override fun tokenInfo(): OAuth2AccessToken? {
                    val errMsg = "找不到对应的用户信息"
                    logAdapter.error(errMsg)
                    return null
                }

                @Throws(ServerException::class)
                override fun hasModuleFunc(moduleFuncCode: String): InfoVo {
                    val errMsg = "找不到对应的authentication信息"
                    logAdapter.error(errMsg)
                    return InfoVo(message = "false")
                }

                @Throws(ServerException::class)
                override fun userInfo(token: String): UserVo? {
                    val errMsg = "该token找不到对应的用户详细信息【$token】"
                    logAdapter.error(errMsg)
                    return null
                }

                @Throws(ServerException::class)
                override fun userInfo(): UserVo? {
                    val errMsg = "找不到对应的用户详细信息"
                    logAdapter.error(errMsg)
                    return null
                }

                @Throws(ServerException::class)
                override fun findUserById(id: String): UserVo {
                    val errMsg = "找不到对应的用户信息"
                    logAdapter.error(errMsg)
                    return UserVo(id = id)
                }

                @Throws(ServerException::class)
                override fun findUserByLoginNo(loginNo: String): UserVo {
                    val errMsg = "找不到对应的用户信息"
                    logAdapter.error(errMsg)
                    return UserVo(loginNo = loginNo)
                }

                @Throws(ServerException::class)
                override fun findModuleFuncByCurrUser(): List<ModuleFuncVo> {
                    val errMsg = "找不到当前用户信息功能权限信息"
                    logAdapter.error(errMsg)
                    return listOf()
                }

                @Throws(ServerException::class)
                override fun findUserListInCurrOrg(roleCode: String): List<UserVo> {
                    val errMsg = "找不到当前部门下对应的用户信息【role=$roleCode】"
                    logAdapter.error(errMsg)
                    return listOf()
                }

                @Throws(ServerException::class)
                override fun findUserListByOrgLevel(orgLevel: String, roleCode: String): List<UserVo> {
                    val errMsg = "找不到对应的用户信息【orgLevel=$orgLevel,role=$roleCode】"
                    logAdapter.error(errMsg)
                    return listOf()
                }

                @Throws(ServerException::class)
                override fun findUserList(orgCode: String, roleCode: String): List<UserVo> {
                    val errMsg = "找不到对应的用户信息【org=$orgCode,role=$roleCode】"
                    logAdapter.error(errMsg)
                    return listOf()
                }

                @Throws(ServerException::class)
                override fun findUserList(roleCode: String): List<UserVo> {
                    val errMsg = "找不到对应的用户信息【role=$roleCode】"
                    logAdapter.error(errMsg)
                    return listOf()
                }

                @Throws(ServerException::class)
                override fun findRuntimeConfigByName(name: String): RuntimeConfigVo {
                    logAdapter.error("获取运行参数失败 【$name】")
                    return RuntimeConfigVo()
                }

                @Throws(ServerException::class)
                override fun findRuntimeConfigMap(): Map<String, RuntimeConfigVo> {
                    logAdapter.error("获取运行参数失败")
                    return mapOf()
                }
            }
        } else {
            val errMsg = "调用 oauth2-server 异常: " + cause?.message
            logAdapter.error(errMsg, cause)
            throw ServerException(errMsg)
        }
    }
}