package pers.acp.admin.oauth.repo

import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.oauth.entity.Role

/**
 * @author zhangbin by 2018-1-17 17:48
 * @since JDK 11
 */
interface RoleRepository : BaseRepository<Role, String> {

    fun findAllByCodeOrderBySortAsc(code: String): MutableList<Role>

    fun findAllByOrderBySortAsc(): MutableList<Role>

    fun findByAppIdOrderBySortAsc(appId: String): MutableList<Role>

    fun findByAppIdAndLevelsGreaterThanOrderBySortAsc(appId: String, level: Int): MutableList<Role>

    fun deleteByIdIn(idList: MutableList<String>)

}
