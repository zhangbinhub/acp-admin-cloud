package pers.acp.admin.oauth.repo

import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.oauth.entity.Application

/**
 * @author zhangbin by 2018-1-17 17:44
 * @since JDK 11
 */
interface ApplicationRepository : BaseRepository<Application, String> {

    fun deleteByIdInAndCovert(idList: MutableList<String>, covert: Boolean)

    fun findAllByOrderByIdentifyAscAppNameAsc(): MutableList<Application>

}
