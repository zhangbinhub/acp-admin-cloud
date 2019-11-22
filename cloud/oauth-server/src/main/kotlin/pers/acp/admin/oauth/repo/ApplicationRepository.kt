package pers.acp.admin.oauth.repo

import pers.acp.admin.oauth.base.OauthBaseRepository
import pers.acp.admin.oauth.entity.Application

/**
 * @author zhangbin by 2018-1-17 17:44
 * @since JDK 11
 */
interface ApplicationRepository : OauthBaseRepository<Application, String> {

    fun deleteByIdInAndCovert(idList: MutableList<String>, covert: Boolean)

    fun findAllByOrderByIdentifyAscAppNameAsc(): MutableList<Application>

}
