package pers.acp.admin.log.domain

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.log.entity.OperateLog
import pers.acp.admin.log.entity.RouteLog
import pers.acp.admin.log.feign.OauthServer
import pers.acp.admin.log.message.RouteLogMessage
import pers.acp.admin.log.po.RouteLogPo
import pers.acp.admin.log.repo.RouteLogRepository
import pers.acp.core.CommonTools
import java.util.*
import javax.persistence.criteria.Predicate

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class LogDomain @Autowired
constructor(private val routeLogRepository: RouteLogRepository,
            private val oauthServer: OauthServer) : BaseDomain() {

    @Transactional
    fun doLoginLog(routeLogMessage: RouteLogMessage) {
        // todo
//        routeLogRepository.save(routeLog)
    }

    @Transactional
    fun doRouteLog(routeLogMessage: RouteLogMessage) {
        GlobalScope.launch {
            val routeLog = RouteLog().also {
                it.logId = routeLogMessage.logId
                it.remoteIp = routeLogMessage.remoteIp
                it.gatewayIp = routeLogMessage.gatewayIp
                it.path = routeLogMessage.path
                it.serverId = routeLogMessage.serverId
                it.targetIp = routeLogMessage.targetIp
                it.targetUri = routeLogMessage.targetUri
                it.targetPath = routeLogMessage.targetPath
                it.method = routeLogMessage.method
                it.token = routeLogMessage.token
                it.requestTime = routeLogMessage.requestTime
                it.processTime = routeLogMessage.processTime
                it.responseTime = routeLogMessage.responseTime
                it.responseStatus = routeLogMessage.responseStatus
            }
            if (routeLogMessage.token != null) {
                oauthServer.appInfo(routeLogMessage.token!!)?.let { app ->
                    routeLog.clientId = app.id
                    routeLog.clientName = app.appName
                    routeLog.identify = app.identify
                }
            }
            if (routeLogMessage.responseStatus != null) {// 响应日志
                var operateRouteLog = routeLogRepository.findByLogIdAndRequestTime(routeLog.logId!!, routeLog.requestTime!!)
                while (operateRouteLog.isEmpty) {
                    delay(1000)
                    operateRouteLog = routeLogRepository.findByLogIdAndRequestTime(routeLog.logId!!, routeLog.requestTime!!)
                }
                operateRouteLog.ifPresent {
                    it.processTime = routeLog.processTime
                    it.responseTime = routeLog.responseTime
                    it.responseStatus = routeLog.responseStatus
                    routeLogRepository.save(it)
                }
            } else {// 请求日志
                routeLogRepository.save(routeLog)
            }
        }
    }

    @Transactional
    fun doOperateLog(routeLogMessage: RouteLogMessage) {
        // todo
//        routeLogRepository.save(routeLog)
    }

    fun doQueryRouteLog(routeLogPo: RouteLogPo): Page<RouteLog> =
            routeLogRepository.findAll({ root, _, criteriaBuilder ->
                val predicateList: MutableList<Predicate> = mutableListOf()
                if (!CommonTools.isNullStr(routeLogPo.remoteIp)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("remoteIp").`as`(String::class.java), "%" + routeLogPo.remoteIp + "%"))
                }
                if (!CommonTools.isNullStr(routeLogPo.gatewayIp)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("gatewayIp").`as`(String::class.java), "%" + routeLogPo.gatewayIp + "%"))
                }
                if (!CommonTools.isNullStr(routeLogPo.path)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("path").`as`(String::class.java), "%" + routeLogPo.path + "%"))
                }
                if (!CommonTools.isNullStr(routeLogPo.serverId)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("serverId").`as`(String::class.java), "%" + routeLogPo.serverId + "%"))
                }
                if (routeLogPo.startTime != null) {
                    predicateList.add(criteriaBuilder.ge(root.get<Any>("requestTime").`as`(Long::class.java), routeLogPo.startTime))
                }
                if (routeLogPo.endTime != null) {
                    predicateList.add(criteriaBuilder.le(root.get<Any>("requestTime").`as`(Long::class.java), routeLogPo.endTime))
                }
                if (routeLogPo.responseStatus != null) {
                    predicateList.add(criteriaBuilder.equal(root.get<Any>("responseStatus").`as`(Long::class.java), routeLogPo.responseStatus))
                }
                criteriaBuilder.and(*predicateList.toTypedArray())
            }, buildPageRequest(routeLogPo.queryParam!!))

}
