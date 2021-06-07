package pers.acp.admin.oauth.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.Role
import pers.acp.admin.oauth.entity.User
import pers.acp.admin.oauth.po.RolePo
import pers.acp.admin.oauth.repo.MenuRepository
import pers.acp.admin.oauth.repo.ModuleFuncRepository
import pers.acp.admin.oauth.repo.RoleRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.admin.oauth.vo.RoleVo
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class RoleDomain @Autowired
constructor(userRepository: UserRepository,
            private val roleRepository: RoleRepository,
            private val menuRepository: MenuRepository,
            private val moduleFuncRepository: ModuleFuncRepository) : OauthBaseDomain(userRepository) {

    @Throws(ServerException::class)
    fun getRoleList(): MutableList<Role> = roleRepository.findAllByOrderBySortAsc()

    @Throws(ServerException::class)
    fun getRoleListByAppId(loginNo: String, appId: String): List<Role> {
        val user = getUserInfoByLoginNo(loginNo) ?: throw ServerException("无法获取当前用户信息")
        return if (isSuper(user)) {
            roleRepository.findByAppIdOrderBySortAsc(appId)
        } else {
            roleRepository.findByAppIdAndLevelsGreaterThanOrderBySortAsc(appId, getRoleMinLevel(appId, user))
        }
    }

    @Throws(ServerException::class)
    private fun doSave(userInfo: User, role: Role, rolePo: RolePo): Role =
            userRepository.findAllById(rolePo.userIds).toMutableSet().let { userSetPo ->
                if (validateModifyUserSet(userInfo, role.userSet, userSetPo)) {
                    roleRepository.save(role.copy(
                            name = rolePo.name!!,
                            code = rolePo.code!!,
                            sort = rolePo.sort,
                            levels = rolePo.levels,
                            userSet = userSetPo,
                            menuSet = menuRepository.findAllById(rolePo.menuIds).toMutableSet(),
                            moduleFuncSet = moduleFuncRepository.findAllById(rolePo.moduleFuncIds).toMutableSet()
                    ))
                } else {
                    throw ServerException("不合法的操作，不允许修改更高级别的用户列表！")
                }
            }

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(loginNo: String, rolePo: RolePo): Role =
            getUserInfoByLoginNo(loginNo)?.let { userInfo ->
                if (!isSuper(userInfo)) {
                    val currLevel = getRoleMinLevel(rolePo.appId!!, userInfo)
                    if (currLevel >= rolePo.levels) {
                        throw ServerException("没有权限做此操作，角色级别必须大于 $currLevel")
                    }
                }
                if (rolePo.code == RoleCode.SUPER) {
                    throw ServerException("不允许创建超级管理员")
                }
                doSave(userInfo, Role(appId = rolePo.appId!!), rolePo)
            } ?: throw ServerException("无法获取当前用户信息")

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(loginNo: String, idList: MutableList<String>) {
        val user = getUserInfoByLoginNo(loginNo) ?: throw ServerException("无法获取当前用户信息")
        if (!isSuper(user)) {
            val roleMinLevel = getRoleMinLevel(user)
            val roleList = roleRepository.findAllById(idList)
            roleList.forEach {
                if (!roleMinLevel.containsKey(it.appId) || roleMinLevel.getValue(it.appId) >= it.levels) {
                    throw ServerException("没有权限做此操作")
                }
            }
        }
        roleRepository.deleteByIdIn(idList)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(loginNo: String, rolePo: RolePo): Role =
            getUserInfoByLoginNo(loginNo)?.let { userInfo ->
                val role = roleRepository.getById(rolePo.id!!)
                if (!isSuper(userInfo)) {
                    val currLevel = getRoleMinLevel(role.appId, userInfo)
                    if (currLevel > 0 && currLevel >= rolePo.levels) {
                        throw ServerException("没有权限做此操作，角色级别必须大于 $currLevel")
                    }
                    if (currLevel > 0 && currLevel >= role.levels) {
                        throw ServerException("没有权限做此操作，请联系系统管理员")
                    }
                } else {
                    if (rolePo.code != role.code && role.code == RoleCode.SUPER) {
                        throw ServerException("超级管理员编码不允许修改")
                    }
                    if (rolePo.levels != role.levels && role.levels <= 0) {
                        throw ServerException("超级管理员级别不允许修改")
                    }
                    if (rolePo.levels != role.levels && rolePo.levels <= 0) {
                        throw ServerException("不允许修改为超级管理员级别[" + rolePo.levels + "]")
                    }
                }
                doSave(userInfo, role, rolePo)
            } ?: throw ServerException("无法获取当前用户信息")

    @Throws(ServerException::class)
    fun getRoleInfo(roleId: String): RoleVo =
            roleRepository.getById(roleId).let { item ->
                RoleVo(
                        id = item.id,
                        appId = item.appId,
                        code = item.code,
                        levels = item.levels,
                        name = item.name,
                        sort = item.sort,
                        userIds = item.userSet.map { it.id }.toMutableList(),
                        menuIds = item.menuSet.map { it.id }.toMutableList(),
                        moduleFuncIds = item.moduleFuncSet.map { it.id }.toMutableList()
                )
            }
}
