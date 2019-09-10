package pers.acp.admin.log.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.log.entity.RouteLog
import pers.acp.admin.log.po.RouteLogPo
import pers.acp.admin.log.repo.RouteLogRepository
import pers.acp.core.CommonTools
import javax.persistence.criteria.Predicate

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

    fun doQueryLog(routeLogPO: RouteLogPo): Page<RouteLog> =
            routeLogRepository.findAll({ root, _, criteriaBuilder ->
                val predicateList: MutableList<Predicate> = mutableListOf()
                if (!CommonTools.isNullStr(routeLogPO.remoteIp)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("remoteIp").`as`(String::class.java), "%" + routeLogPO.remoteIp + "%"))
                }
                if (!CommonTools.isNullStr(routeLogPO.gatewayIp)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("gatewayIp").`as`(String::class.java), "%" + routeLogPO.gatewayIp + "%"))
                }
                if (!CommonTools.isNullStr(routeLogPO.path)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("path").`as`(String::class.java), "%" + routeLogPO.path + "%"))
                }
                if (!CommonTools.isNullStr(routeLogPO.serverId)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("serverId").`as`(String::class.java), "%" + routeLogPO.serverId + "%"))
                }
                if (routeLogPO.startTime != null) {
                    predicateList.add(criteriaBuilder.ge(root.get<Any>("requestTime").`as`(Long::class.java), routeLogPO.startTime))
                }
                if (routeLogPO.endTime != null) {
                    predicateList.add(criteriaBuilder.le(root.get<Any>("requestTime").`as`(Long::class.java), routeLogPO.endTime))
                }
                if (routeLogPO.responseStatus != null) {
                    predicateList.add(criteriaBuilder.equal(root.get<Any>("responseStatus").`as`(Long::class.java), routeLogPO.responseStatus))
                }
                criteriaBuilder.and(*predicateList.toTypedArray())
            }, buildPageRequest(routeLogPO.queryParam!!))

}
