package pers.acp.admin.oauth.repo

import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.oauth.entity.RuntimeConfig
import java.util.Optional

/**
 * @author zhangbin by 2018-1-16 23:46
 * @since JDK 11
 */
interface RuntimeConfigRepository : BaseRepository<RuntimeConfig, String> {

    fun findByName(name: String): Optional<RuntimeConfig>

    fun deleteByIdInAndCovert(idList: MutableList<String>, covert: Boolean)

}
