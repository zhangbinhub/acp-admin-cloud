package pers.acp.admin.oauth.base

import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.oauth.entity.User
import pers.acp.admin.oauth.repo.UserRepository

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
                    user.roleSet.map { it.levels }.toIntArray().min() == 0

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

}
