package pers.acp.admin.log.domain

import com.fasterxml.jackson.databind.ObjectMapper
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
import pers.acp.admin.log.repo.OperateLogRepository
import pers.acp.admin.log.repo.RouteLogRepository
import pers.acp.core.CommonTools
import pers.acp.spring.boot.interfaces.LogAdapter
import javax.persistence.criteria.Predicate

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class LogDomain @Autowired
constructor(private val logAdapter: LogAdapter,
            private val objectMapper: ObjectMapper,
            private val routeLogRepository: RouteLogRepository,
            private val operateLogRepository: OperateLogRepository,
            private val oauthServer: OauthServer) : BaseDomain() {

    /**
     * 路由消息转为对应的实体对象
     */
    private fun <T> messageToEntity(message: String, cls: Class<T>): T {
        return objectMapper.readValue(message, cls)
    }

    @Transactional
    fun doLoginLog(routeLogMessage: RouteLogMessage, message: String) {
        GlobalScope.launch {
            while (true) {
                try {
                    // todo
                    break
                } catch (e: Exception) {
                    logAdapter.error(e.message, e)
                    delay(5000)
                }
            }
        }
    }

    @Transactional
    fun doRouteLog(routeLogMessage: RouteLogMessage, message: String) {
        GlobalScope.launch {
            while (true) {
                try {
                    val routeLog = messageToEntity(message, RouteLog::class.java)
                    if (routeLogMessage.token != null) {
                        oauthServer.appInfo(routeLogMessage.token!!).let { app ->
                            routeLog.clientId = app.id
                            routeLog.clientName = app.appName
                            routeLog.identify = app.identify
                        }
                    }
                    if (routeLogMessage.responseStatus != null) {// 响应日志
                        var optionalRouteLog = routeLogRepository.findByLogIdAndRequestTime(routeLog.logId!!, routeLog.requestTime!!)
                        while (optionalRouteLog.isEmpty) {
                            delay(1000)
                            optionalRouteLog = routeLogRepository.findByLogIdAndRequestTime(routeLog.logId!!, routeLog.requestTime!!)
                        }
                        optionalRouteLog.ifPresent {
                            it.processTime = routeLog.processTime
                            it.responseTime = routeLog.responseTime
                            it.responseStatus = routeLog.responseStatus
                            routeLogRepository.save(it)
                        }
                    } else {// 请求日志
                        routeLogRepository.save(routeLog)
                    }
                    break
                } catch (e: Exception) {
                    logAdapter.error(e.message, e)
                    delay(5000)
                }
            }
        }
    }

    @Transactional
    fun doOperateLog(routeLogMessage: RouteLogMessage, message: String) {
        GlobalScope.launch {
            while (true) {
                try {
                    val operateLog = messageToEntity(message, OperateLog::class.java)
                    if (routeLogMessage.token != null && routeLogMessage.responseStatus != null) {
                        oauthServer.appInfo(routeLogMessage.token!!).let { app ->
                            operateLog.clientId = app.id
                            operateLog.clientName = app.appName
                            operateLog.identify = app.identify
                        }
                        oauthServer.userInfo(routeLogMessage.token!!).let { user ->
                            operateLog.userId = user.id
                            operateLog.loginNo = user.loginNo
                            operateLog.userName = user.name
                        }
                        operateLogRepository.save(operateLog)
                    }
                    break
                } catch (e: Exception) {
                    logAdapter.error(e.message, e)
                    delay(5000)
                }
            }
        }
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
