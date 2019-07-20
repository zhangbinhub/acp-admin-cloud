package pers.acp.admin.oauth.repo

import pers.acp.admin.oauth.base.OauthBaseRepository
import pers.acp.admin.oauth.entity.ModuleFunc

/**
 * @author zhangbin by 2018-1-17 17:46
 * @since JDK 11
 */
interface ModuleFuncRepository : OauthBaseRepository<ModuleFunc, String> {

    fun findByAppId(appId: String): MutableList<ModuleFunc>

    fun findByParentIdIn(idList: MutableList<String>): MutableList<ModuleFunc>

    fun deleteByIdInAndCovert(idList: MutableList<String>, covert: Boolean)

}