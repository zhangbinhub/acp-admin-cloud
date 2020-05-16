package pers.acp.admin.log.repo

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.log.entity.LoginLogHistory

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
interface LoginLogHistoryRepository : BaseRepository<LoginLogHistory, String> {
    @Query("select clientId,clientName,loginDate,count(id) from LoginLogHistory where requestTime>=:beginTime group by clientId,clientName,loginDate")
    fun loginStatistics(@Param("beginTime") beginTime: Long): MutableList<Array<Any>>

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from LoginLogHistory where requestTime<:time")
    fun deleteAllByRequestTimeLessThan(@Param("time") time: Long)
}
