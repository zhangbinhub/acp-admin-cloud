package pers.acp.admin.route.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.route.entity.RouteLog
import pers.acp.admin.route.repo.RouteLogRepository

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class RouteLogDomain @Autowired
constructor(private val routeLogRepository: RouteLogRepository) : BaseDomain() {

    @Transactional
    fun doLog(routeLog: RouteLog) {
        routeLogRepository.save(routeLog)
    }

}
