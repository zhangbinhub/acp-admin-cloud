package pers.acp.admin.oauth.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.ModuleFunc
import pers.acp.admin.oauth.entity.User
import pers.acp.admin.oauth.po.ModuleFuncPo
import pers.acp.admin.oauth.repo.ModuleFuncRepository
import pers.acp.admin.oauth.repo.RoleRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.admin.oauth.vo.ModuleFuncVo
import io.github.zhangbinhub.acp.boot.exceptions.ServerException

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class ModuleFuncDomain @Autowired
constructor(
    userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val moduleFuncRepository: ModuleFuncRepository
) : OauthBaseDomain(userRepository) {

    fun getAllModuleFuncList(): MutableList<ModuleFunc> =
        moduleFuncRepository.findAll().let {
            val map: MutableMap<String, ModuleFunc> = mutableMapOf()
            it.forEach { item ->
                map[item.id] = item
            }
            sortModuleFuncList(formatToTreeList(map))
        }

    fun getModuleFuncList(userId: String): MutableList<ModuleFunc> =
        userRepository.getById(userId).let {
            val moduleFuncIds: MutableSet<String> = mutableSetOf()
            it.roleSet.flatMap { item -> item.moduleFuncSet }
                .filter { item ->
                    if (moduleFuncIds.contains(item.id)) {
                        false
                    } else {
                        moduleFuncIds.add(item.id)
                        true
                    }
                }
                .toMutableList()
        }

    fun getModuleFuncList(appId: String, loginNo: String): MutableList<ModuleFunc> =
        (getUserInfoByLoginNo(loginNo) ?: throw ServerException("无法获取当前用户信息")).let {
            val moduleFuncIds: MutableSet<String> = mutableSetOf()
            it.roleSet.filter { role -> role.appId == appId }
                .flatMap { item -> item.moduleFuncSet }
                .filter { item ->
                    if (moduleFuncIds.contains(item.id)) {
                        false
                    } else {
                        moduleFuncIds.add(item.id)
                        true
                    }
                }
                .filter { menu -> menu.appId == appId }
                .toMutableList()
        }

    fun hasModuleFunc(appId: String, loginNo: String, moduleFuncCode: String): Boolean =
        getModuleFuncList(appId, loginNo).any { item -> item.code == moduleFuncCode }

    fun hasModuleFunc(userId: String, moduleFuncCode: String): Boolean =
        getModuleFuncList(userId).any { item -> item.code == moduleFuncCode }

    private fun sortModuleFuncList(moduleFuncList: MutableList<ModuleFunc>): MutableList<ModuleFunc> =
        moduleFuncList.let { list ->
            list.forEach { organization ->
                if (organization.children.isNotEmpty()) {
                    sortModuleFuncList(organization.children)
                }
            }
            moduleFuncList.apply {
                this.sortBy { it.code }
            }
        }

    fun getModuleFuncListByAppId(appId: String): List<ModuleFunc> =
        moduleFuncRepository.findByAppId(appId).let {
            val map: MutableMap<String, ModuleFunc> = mutableMapOf()
            it.forEach { item ->
                map[item.id] = item
            }
            sortModuleFuncList(formatToTreeList(map))
        }

    @Throws(ServerException::class)
    private fun doSave(userInfo: User, moduleFunc: ModuleFunc, moduleFuncPo: ModuleFuncPo): ModuleFunc =
        roleRepository.findAllById(moduleFuncPo.roleIds).toMutableSet().let { roleSetPo ->
            if (validateModifyRoleSet(userInfo, moduleFuncPo.appId!!, moduleFunc.roleSet, roleSetPo)) {
                moduleFuncRepository.save(moduleFunc.copy(
                    name = moduleFuncPo.name!!,
                    code = moduleFuncPo.code!!,
                    roleSet = roleSetPo
                ).apply {
                    parentId = moduleFuncPo.parentId!!
                })
            } else {
                throw ServerException("不合法的操作，不允许修改更高级别的角色列表！")
            }
        }

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(user: OAuth2Authentication, moduleFuncPo: ModuleFuncPo): ModuleFunc =
        moduleFuncRepository.findByCode(moduleFuncPo.code!!).let {
            if (it.isPresent) {
                throw ServerException("编码重复")
            }
            getUserInfoByLoginNo(user.name)?.let { userInfo ->
                doSave(
                    userInfo, ModuleFunc(
                        appId = moduleFuncPo.appId!!,
                        covert = true
                    ), moduleFuncPo
                )
            } ?: throw ServerException("无法获取当前用户信息")
        }

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(idList: MutableList<String>) {
        moduleFuncRepository.findByParentIdIn(idList).apply {
            if (this.isNotEmpty()) {
                throw ServerException("存在下级模块功能，不允许删除")
            }
        }
        moduleFuncRepository.deleteByIdInAndCovert(idList, true)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(user: OAuth2Authentication, moduleFuncPo: ModuleFuncPo): ModuleFunc =
        moduleFuncRepository.findByCodeAndIdNot(moduleFuncPo.code!!, moduleFuncPo.id!!).let {
            if (it.isPresent) {
                throw ServerException("编码重复")
            }
            getUserInfoByLoginNo(user.name)?.let { userInfo ->
                doSave(userInfo, moduleFuncRepository.getById(moduleFuncPo.id!!), moduleFuncPo)
            } ?: throw ServerException("无法获取当前用户信息")
        }

    @Throws(ServerException::class)
    fun getModuleFuncInfo(moduleFuncId: String): ModuleFuncVo =
        moduleFuncRepository.getById(moduleFuncId).let { item ->
            ModuleFuncVo(
                id = item.id,
                appId = item.appId,
                code = item.code,
                name = item.name,
                parentId = item.parentId,
                roleIds = item.roleSet.map { it.id }.toMutableList()
            )
        }

}
