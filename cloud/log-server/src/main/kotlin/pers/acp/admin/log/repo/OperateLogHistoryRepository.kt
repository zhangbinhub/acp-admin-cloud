package pers.acp.admin.log.repo

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.log.entity.OperateLogHistory

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
interface OperateLogHistoryRepository : BaseRepository<OperateLogHistory, String> {
    @Modifying(clearAutomatically = true)
    @Query("delete from OperateLogHistory where requestTime<:time")
    fun deleteAllByRequestTimeLessThan(@Param("time") time: Long)
}
