package pers.acp.admin.oauth.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.ModuleFunc
import pers.acp.admin.oauth.po.ModuleFuncPo
import pers.acp.admin.oauth.repo.ModuleFuncRepository
import pers.acp.admin.oauth.repo.RoleRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.admin.oauth.vo.ModuleFuncVo
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class ModuleFuncDomain @Autowired
constructor(userRepository: UserRepository,
            private val roleRepository: RoleRepository,
            private val moduleFuncRepository: ModuleFuncRepository) : OauthBaseDomain(userRepository) {

    fun getAllModuleFuncList(): MutableList<ModuleFunc> =
            moduleFuncRepository.findAll().let {
                val map: MutableMap<String, ModuleFunc> = mutableMapOf()
                it.forEach { item ->
                    map[item.id] = item
                }
                sortModuleFuncList(formatToTreeList(map))
            }

    private fun sortModuleFuncList(moduleFuncList: MutableList<ModuleFunc>): MutableList<ModuleFunc> =
            moduleFuncList.let { list ->
                list.forEach { organization ->
                    if (organization.children.isNotEmpty()) {
                        sortModuleFuncList(organization.children)
                    }
                }
                moduleFuncList.sortBy { it.code }
                moduleFuncList
            }

    fun getModuleFuncListByAppId(appId: String): List<ModuleFunc> =
            moduleFuncRepository.findByAppId(appId).let {
                val map: MutableMap<String, ModuleFunc> = mutableMapOf()
                it.forEach { item ->
                    map[item.id] = item
                }
                sortModuleFuncList(formatToTreeList(map))
            }

    private fun doSave(moduleFunc: ModuleFunc, moduleFuncPO: ModuleFuncPo): ModuleFunc =
            moduleFuncRepository.save(moduleFunc.apply {
                name = moduleFuncPO.name!!
                code = moduleFuncPO.code!!
                roleSet = roleRepository.findAllById(moduleFuncPO.roleIds).toMutableSet()
                parentId = moduleFuncPO.parentId!!
            })

    @Transactional
    fun doCreate(moduleFuncPO: ModuleFuncPo): ModuleFunc =
            doSave(ModuleFunc().apply {
                appId = moduleFuncPO.appId!!
                covert = true
            }, moduleFuncPO)

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(idList: MutableList<String>) {
        val menuList = moduleFuncRepository.findByParentIdIn(idList)
        if (menuList.isNotEmpty()) {
            throw ServerException("存在下级模块功能，不允许删除")
        }
        moduleFuncRepository.deleteByIdInAndCovert(idList, true)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(moduleFuncPO: ModuleFuncPo): ModuleFunc {
        val moduleFuncOptional = moduleFuncRepository.findById(moduleFuncPO.id!!)
        if (moduleFuncOptional.isEmpty) {
            throw ServerException("找不到模块功能信息")
        }
        return doSave(moduleFuncOptional.get(), moduleFuncPO)
    }

    @Throws(ServerException::class)
    fun getModuleFuncInfo(moduleFuncId: String): ModuleFuncVo {
        val moduleFuncOptional = moduleFuncRepository.findById(moduleFuncId)
        if (moduleFuncOptional.isEmpty) {
            throw ServerException("找不到模块功能信息")
        }
        return moduleFuncOptional.get().let { item ->
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

}
