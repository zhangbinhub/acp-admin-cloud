package pers.acp.admin.log.repo

import org.springframework.data.jpa.repository.Query
import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.log.entity.LoginLog

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
interface LoginLogRepository : BaseRepository<LoginLog, String> {
    @Query("select clientId,clientName,loginDate,count(id) from LoginLog group by clientId,clientName,loginDate")
    fun loginStatistics(): MutableList<Array<Any>>
}
