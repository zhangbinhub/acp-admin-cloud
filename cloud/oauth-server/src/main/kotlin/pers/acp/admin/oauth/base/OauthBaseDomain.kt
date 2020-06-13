package pers.acp.admin.oauth.base

import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.oauth.entity.Organization
import pers.acp.admin.oauth.entity.Role
import pers.acp.admin.oauth.entity.User
import pers.acp.admin.oauth.repo.OrganizationRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@Transactional(readOnly = true)
abstract class OauthBaseDomain(protected val userRepository: UserRepository) : BaseDomain() {

    fun findCurrUserInfo(loginNo: String): User? = userRepository.findByLoginNo(loginNo).orElse(null)

    /**
     * 判断指定用户是否是超级管理员
     *
     * @param user 用户对象
     * @return true|false
     */
    protected fun isSuper(user: User): Boolean =
            user.roleSet.map { it.code }.toList().contains(RoleCode.SUPER) ||
                    (user.roleSet.isNotEmpty() && user.roleSet.map { it.levels }.toIntArray().min() == 0)

    /**
     * 获取指定用户所属角色中最高级别
     *
     * @param appId 应用ID
     * @param user  用户对象
     * @return 级别
     */
    protected fun getRoleMinLevel(appId: String, user: User): Int =
            user.let {
                var level = Int.MAX_VALUE
                it.roleSet.filter { role -> role.appId == appId }.let { item ->
                    item.forEach { role ->
                        if (level > role.levels) {
                            level = role.levels
                        }
                    }
                    level
                }
            }

    @Throws(ServerException::class)
    protected fun validateModifyRoleSet(userInfo: User, appId: String, oldRoleSet: MutableSet<Role>, newRoleSet: MutableSet<Role>): Boolean =
            if (!isSuper(userInfo)) {
                val roleMinLevel = getRoleMinLevel(appId, userInfo)
                val oldSuperRoles = oldRoleSet.filter { role -> role.levels <= roleMinLevel }.map { it.id }.sorted()
                val newSuperRoles = newRoleSet.filter { role -> role.levels <= roleMinLevel }.map { it.id }.sorted()
                if (oldSuperRoles.size != newSuperRoles.size) {
                    throw ServerException("不合法的操作，不允许修改更高级别的角色列表！")
                }
                for (index in oldSuperRoles.indices) {
                    if (oldSuperRoles[index] != newSuperRoles[index]) {
                        throw ServerException("不合法的操作，不允许修改更高级别的角色列表！")
                    }
                }
                true
            } else {
                true
            }

    @Throws(ServerException::class)
    protected fun validateModifyUserSet(userInfo: User, oldUserSet: MutableSet<User>, newUserSet: MutableSet<User>): Boolean =
            if (!isSuper(userInfo)) {
                val oldSuperUsers = oldUserSet.filter { user -> user.levels <= userInfo.levels }.map { it.id }.sorted()
                val newSuperUsers = newUserSet.filter { user -> user.levels <= userInfo.levels }.map { it.id }.sorted()
                if (oldSuperUsers.size != newSuperUsers.size) {
                    throw ServerException("不合法的操作，不允许修改更高级别的用户列表！")
                }
                for (index in oldSuperUsers.indices) {
                    if (oldSuperUsers[index] != newSuperUsers[index]) {
                        throw ServerException("不合法的操作，不允许修改更高级别的用户列表！")
                    }
                }
                true
            } else {
                true
            }

    /**
     * 获取指定用户所属角色中最高级别
     *
     * @param user 用户对象
     * @return 级别
     */
    protected fun getRoleMinLevel(user: User): Map<String, Int> {
        val minMap: MutableMap<String, Int> = mutableMapOf()
        user.roleSet.forEach { role ->
            if (minMap.containsKey(role.appId)) {
                if (minMap.getValue(role.appId) > role.levels) {
                    minMap[role.appId] = role.levels
                }
            } else {
                minMap[role.appId] = role.levels
            }
        }
        return minMap
    }

    protected fun <T : OauthBaseTreeEntity<T>> formatToTreeList(map: Map<String, T>): MutableList<T> {
        val result: MutableList<T> = mutableListOf()
        map.forEach { (_, value) ->
            if (map.containsKey(value.parentId)) {
                map.getValue(value.parentId).children.add(value)
            } else {
                result.add(value)
            }
        }
        return result
    }

    protected fun getAllOrgList(organizationRepository: OrganizationRepository,
                                organizationList: MutableList<Organization>): MutableList<Organization> =
            mutableListOf<Organization>().let {
                it.addAll(organizationList)
                var children = getChildrenOrgList(organizationRepository, it)
                while (children.isNotEmpty()) {
                    it.addAll(children)
                    children = getChildrenOrgList(organizationRepository, children)
                }
                getOrgListDistinct(it)
            }

    /**
     * 获取指定机构集合的所有子机构
     */
    private fun getChildrenOrgList(organizationRepository: OrganizationRepository,
                                   organizationList: MutableList<Organization>): MutableList<Organization> =
            mutableListOf<Organization>().apply {
                organizationList.map { org -> org.id }.toMutableList().let {
                    this.addAll(organizationRepository.findByParentIdIn(it))
                }
            }

    /**
     * Organization集合去重，返回List
     */
    private fun getOrgListDistinct(organizations: MutableList<Organization>): MutableList<Organization> =
            mutableListOf<Organization>().apply {
                val orgIdList = mutableListOf<String>()
                organizations.forEach { organization ->
                    if (!orgIdList.contains(organization.id)) {
                        this.add(organization)
                        orgIdList.add(organization.id)
                    }
                }
            }
}
