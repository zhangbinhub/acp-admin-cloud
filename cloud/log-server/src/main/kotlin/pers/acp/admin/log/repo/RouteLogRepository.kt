package pers.acp.admin.log.repo

import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.log.entity.RouteLog
import java.util.*

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
interface RouteLogRepository : BaseRepository<RouteLog, String> {

    fun findByLogIdAndRequestTime(logId: String, requestParam: Long): Optional<RouteLog>
}
