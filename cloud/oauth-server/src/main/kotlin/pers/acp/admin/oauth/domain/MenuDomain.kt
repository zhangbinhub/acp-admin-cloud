package pers.acp.admin.oauth.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.Menu
import pers.acp.admin.oauth.po.MenuPo
import pers.acp.admin.oauth.repo.MenuRepository
import pers.acp.admin.oauth.repo.RoleRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.admin.oauth.vo.MenuVo
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class MenuDomain @Autowired
constructor(userRepository: UserRepository, private val roleRepository: RoleRepository, private val menuRepository: MenuRepository) : OauthBaseDomain(userRepository) {

    fun getAllMenuList(): MutableList<Menu> =
            menuRepository.findAll().let {
                val map: MutableMap<String, Menu> = mutableMapOf()
                it.forEach { item ->
                    map[item.id] = item
                }
                sortMenuList(formatToTreeList(map))
            }

    private fun sortMenuList(menuList: MutableList<Menu>): MutableList<Menu> =
            menuList.let { list ->
                list.forEach { menu ->
                    if (menu.children.isNotEmpty()) {
                        sortMenuList(menu.children)
                    }
                }
                menuList.apply {
                    this.sortBy { it.sort }
                }
            }

    fun getMenuList(appId: String, loginNo: String): MutableList<Menu> =
            (findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息")).let {
                val menuIds: MutableSet<String> = mutableSetOf()
                it.roleSet.filter { role -> role.appId == appId }
                        .flatMap { item -> item.menuSet }
                        .filter { item ->
                            if (menuIds.contains(item.id) || !item.enabled) {
                                false
                            } else {
                                menuIds.add(item.id)
                                true
                            }
                        }
                        .filter { menu -> menu.appId == appId }
                        .let { menu ->
                            val map: MutableMap<String, Menu> = mutableMapOf()
                            menu.forEach { item ->
                                map[item.id] = item
                            }
                            map
                        }.let { map ->
                            val result: MutableList<Menu> = mutableListOf()
                            map.forEach { (_, menu) ->
                                if (map.containsKey(menu.parentId)) {
                                    map.getValue(menu.parentId).children.add(menu)
                                } else if (menu.parentId == menu.appId) {
                                    result.add(menu)
                                }
                            }
                            sortMenuList(result)
                        }
            }

    fun getMenuListByAppId(appId: String): MutableList<Menu> =
            menuRepository.findByAppId(appId).let {
                val map: MutableMap<String, Menu> = mutableMapOf()
                it.forEach { item ->
                    map[item.id] = item
                }
                sortMenuList(formatToTreeList(map))
            }

    private fun doSave(menu: Menu, menuPo: MenuPo): Menu =
            menuRepository.save(menu.copy(
                    path = menuPo.path,
                    enabled = menuPo.enabled,
                    iconType = menuPo.iconType,
                    name = menuPo.name!!,
                    openType = menuPo.openType,
                    sort = menuPo.sort,
                    roleSet = roleRepository.findAllById(menuPo.roleIds).toMutableSet()
            ).apply {
                parentId = menuPo.parentId!!
            })

    @Transactional
    fun doCreate(menuPo: MenuPo): Menu =
            doSave(Menu(
                    appId = menuPo.appId!!,
                    covert = true
            ), menuPo)

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(idList: MutableList<String>) {
        val menuList = menuRepository.findByParentIdIn(idList)
        if (menuList.isNotEmpty()) {
            throw ServerException("存在下级菜单，不允许删除")
        }
        menuRepository.deleteByIdInAndCovert(idList, true)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(menuPo: MenuPo): Menu = doSave(menuRepository.getOne(menuPo.id!!), menuPo)

    @Throws(ServerException::class)
    fun getMenuInfo(menuId: String): MenuVo =
            menuRepository.getOne(menuId).let { item ->
                MenuVo(
                        id = item.id,
                        appId = item.appId,
                        enabled = item.enabled,
                        iconType = item.iconType,
                        name = item.name,
                        openType = item.openType,
                        parentId = item.parentId,
                        path = item.path,
                        sort = item.sort,
                        roleIds = item.roleSet.map { it.id }.toMutableList()
                )
            }

}
