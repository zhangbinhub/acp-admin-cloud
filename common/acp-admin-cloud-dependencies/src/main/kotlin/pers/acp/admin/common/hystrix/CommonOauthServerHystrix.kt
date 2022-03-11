package pers.acp.admin.common.hystrix

import com.fasterxml.jackson.databind.ObjectMapper
import feign.FeignException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import org.springframework.security.oauth2.common.OAuth2AccessToken
import pers.acp.admin.common.base.BaseFeignHystrix
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.common.vo.*

/**
 * @author zhang by 14/11/2019
 * @since JDK 11
 */
class CommonOauthServerHystrix
constructor(logAdapter: LogAdapter, objectMapper: ObjectMapper) :
    BaseFeignHystrix<CommonOauthServer>(logAdapter, objectMapper) {
    override fun create(cause: Throwable?): CommonOauthServer {
        val message = if (cause is FeignException.Unauthorized || cause is FeignException.Forbidden) {
            logAdapter.error("token无效")
            "token无效"
        } else {
            val errMsg = "调用 oauth2-server 异常: " + cause?.message
            logAdapter.error(errMsg)
            getErrorMessage(cause)
        }
        return object : CommonOauthServer {
            override fun appInfo(token: String): ApplicationVo {
                val errMsg = "该token找不到对应的应用信息【$token】"
                logAdapter.error(errMsg)
                return ApplicationVo()
            }

            override fun tokenInfo(token: String): OAuth2AccessToken? {
                val errMsg = "该token找不到对应的用户信息【$token】"
                logAdapter.error(errMsg)
                return null
            }

            override fun tokenInfo(): OAuth2AccessToken? {
                val errMsg = "找不到对应的用户信息"
                logAdapter.error(errMsg)
                return null
            }

            override fun hasModuleFunc(moduleFuncCode: String): BooleanInfoVo {
                val errMsg = "找不到对应的功能权限信息"
                logAdapter.error(errMsg)
                return BooleanInfoVo(result = false)
            }

            override fun hasModuleFunc(userId: String, moduleFuncCode: String): BooleanInfoVo {
                val errMsg = "找不到对应的功能权限信息"
                logAdapter.error(errMsg)
                return BooleanInfoVo(result = false)
            }

            override fun orgList(): List<OrganizationVo> {
                val errMsg = "获取机构列表失败"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun currAndAllChildrenForOrg(): List<OrganizationVo> {
                val errMsg = "获取机构列表失败"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun currAndAllChildrenForMngOrg(): List<OrganizationVo> {
                val errMsg = "获取机构列表失败"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun currAndAllChildrenForAllOrg(): List<OrganizationVo> {
                val errMsg = "获取机构列表失败"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun userInfo(token: String): UserVo? {
                val errMsg = "该token找不到对应的用户详细信息【$token】"
                logAdapter.error(errMsg)
                return null
            }

            override fun userInfo(): UserVo? {
                val errMsg = "找不到对应的用户详细信息"
                logAdapter.error(errMsg)
                return null
            }

            override fun disableUser(loginNo: String): InnerInfoVo {
                val errMsg = "禁用用户【$loginNo】失败：$message"
                logAdapter.error(errMsg)
                return InnerInfoVo(success = false, message = errMsg)
            }

            override fun findUserById(id: String): UserVo {
                val errMsg = "找不到对应的用户信息"
                logAdapter.error(errMsg)
                return UserVo(id = id)
            }

            override fun findUserByLoginNo(loginNo: String): UserVo {
                val errMsg = "找不到对应的用户信息"
                logAdapter.error(errMsg)
                return UserVo(loginNo = loginNo)
            }

            override fun findModuleFuncByCurrUser(): List<ModuleFuncVo> {
                val errMsg = "找不到当前用户信息功能权限信息"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun findUserListInCurrOrg(roleCode: String): List<UserVo> {
                val errMsg = "找不到当前部门下对应的用户信息【role=$roleCode】"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun findUserListByOrgLevelByCurrUser(orgLevel: String, roleCode: String): List<UserVo> {
                val errMsg = "找不到对应的用户信息【orgLevel=$orgLevel,role=$roleCode】"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun findUserList(idList: List<String>): List<UserVo> {
                val errMsg = "找不到对应的用户信息"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun findUserList(orgCode: String, roleCode: String): List<UserVo> {
                val errMsg = "找不到对应的用户信息【org=$orgCode,role=$roleCode】"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun findUserList(roleCode: String): List<UserVo> {
                val errMsg = "找不到对应的用户信息【role=$roleCode】"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun findUserList(loginNo: String, orgLevel: String, roleCode: String): List<UserVo> {
                val errMsg = "找不到对应的用户信息【orgLevel=$orgLevel,role=$roleCode】"
                logAdapter.error(errMsg)
                return listOf()
            }

            override fun findRuntimeConfigByName(name: String): RuntimeConfigVo {
                logAdapter.error("获取运行参数失败 【$name】")
                return RuntimeConfigVo()
            }

            override fun findRuntimeConfigMap(): Map<String, RuntimeConfigVo> {
                logAdapter.error("获取运行参数失败")
                return mapOf()
            }
        }
    }
}