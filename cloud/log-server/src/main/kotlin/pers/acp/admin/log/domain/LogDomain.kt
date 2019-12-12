package pers.acp.admin.log.domain

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.constant.TokenConstant
import pers.acp.admin.log.constant.LogConstant
import pers.acp.admin.log.entity.LoginLog
import pers.acp.admin.log.entity.OperateLog
import pers.acp.admin.log.entity.RouteLog
import pers.acp.admin.log.message.RouteLogMessage
import pers.acp.admin.log.po.LoginLogQueryPo
import pers.acp.admin.log.po.OperateLogQueryPo
import pers.acp.admin.log.po.RouteLogQueryPo
import pers.acp.admin.log.repo.*
import pers.acp.admin.log.vo.LoginLogVo
import pers.acp.core.CommonTools
import pers.acp.core.task.timer.Calculation
import pers.acp.spring.boot.exceptions.ServerException
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
            private val routeLogHistoryRepository: RouteLogHistoryRepository,
            private val operateLogRepository: OperateLogRepository,
            private val operateLogHistoryRepository: OperateLogHistoryRepository,
            private val loginLogRepository: LoginLogRepository,
            private val loginLogHistoryRepository: LoginLogHistoryRepository,
            private val commonOauthServer: CommonOauthServer) : BaseDomain() {

    /**
     * 路由消息转为对应的实体对象
     */
    private fun <T> messageToEntity(message: String, cls: Class<T>): T {
        return objectMapper.readValue(message, cls) ?: throw ServerException("日志消息转换失败")
    }

    private fun getTokenInfo(token: String): Map<String, String> =
            commonOauthServer.tokenInfo(token)?.let { oAuth2AccessToken ->
                if (oAuth2AccessToken.additionalInformation.containsKey(TokenConstant.USER_INFO_ID)) {
                    mutableMapOf<String, String>().apply {
                        this[TokenConstant.USER_INFO_ID] = oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_ID]!!.toString()
                        this[TokenConstant.USER_INFO_LOGIN_NO] = oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_LOGIN_NO]!!.toString()
                        this[TokenConstant.USER_INFO_NAME] = oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_NAME]!!.toString()
                    }
                } else {
                    mapOf<String, String>()
                }
            } ?: mapOf()

    fun loginStatistics(beginTime: Long): List<LoginLogVo> =
            loginLogHistoryRepository.loginStatistics(beginTime).let {
                it.addAll(loginLogRepository.loginStatistics())
                it.map { item ->
                    LoginLogVo(item[0].toString(), item[1].toString(), item[2].toString(), item[3].toString().toLong())
                }
            }

    @Transactional
    suspend fun doRouteLog(routeLogMessage: RouteLogMessage, message: String) {
        val routeLog = messageToEntity(message, RouteLog::class.java)
        if (routeLogMessage.token != null) {
            try {
                commonOauthServer.appInfo(routeLogMessage.token!!).let { app ->
                    routeLog.clientId = app.id
                    routeLog.clientName = app.appName
                    routeLog.identify = app.identify
                    if (!CommonTools.isNullStr(app.id)) {
                        getTokenInfo(routeLogMessage.token!!).let {
                            if (it.isNotEmpty()) {
                                routeLog.userId = it[TokenConstant.USER_INFO_ID]
                                routeLog.loginNo = it[TokenConstant.USER_INFO_LOGIN_NO]
                                routeLog.userName = it[TokenConstant.USER_INFO_NAME]
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
            }
        }
        if (routeLogMessage.responseStatus != null) {// 响应日志
            var optionalRouteLog = routeLogRepository.findByLogIdAndRequestTime(routeLog.logId, routeLog.requestTime)
            var count = 1
            while (optionalRouteLog.isEmpty) {
                delay(LogConstant.ROUTE_LOG_QUERY_INTERVAL_TIME)
                optionalRouteLog = routeLogRepository.findByLogIdAndRequestTime(routeLog.logId, routeLog.requestTime)
                if (++count >= LogConstant.ROUTE_LOG_QUERY_MAX_NUMBER) {
                    break
                }
            }
            optionalRouteLog.ifPresent {
                it.token = routeLog.token
                it.clientId = routeLog.clientId
                it.clientName = routeLog.clientName
                it.identify = routeLog.identify
                it.userId = routeLog.userId
                it.loginNo = routeLog.loginNo
                it.userName = routeLog.userName
                it.processTime = routeLog.processTime
                it.responseTime = routeLog.responseTime
                it.responseStatus = routeLog.responseStatus
                routeLogRepository.save(it)
            }
        } else {// 请求日志
            routeLogRepository.save(routeLog)
        }
    }

    @Transactional
    suspend fun doOperateLog(routeLogMessage: RouteLogMessage, message: String) {
        try {
            val operateLog = messageToEntity(message, OperateLog::class.java)
            commonOauthServer.appInfo(routeLogMessage.token!!).let { app ->
                operateLog.clientId = app.id
                operateLog.clientName = app.appName
                operateLog.identify = app.identify
                if (!CommonTools.isNullStr(app.id)) {
                    getTokenInfo(routeLogMessage.token!!).let {
                        if (it.isNotEmpty()) {
                            operateLog.userId = it[TokenConstant.USER_INFO_ID]
                            operateLog.loginNo = it[TokenConstant.USER_INFO_LOGIN_NO]
                            operateLog.userName = it[TokenConstant.USER_INFO_NAME]
                        }
                    }
                    operateLogRepository.save(operateLog)
                }
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException("操作日志记录失败")
        }
    }

    @Transactional
    suspend fun doLoginLog(routeLogMessage: RouteLogMessage, message: String) {
        try {
            val loginLog = messageToEntity(message, LoginLog::class.java)
            commonOauthServer.appInfo(routeLogMessage.token!!).let { app ->
                if (!CommonTools.isNullStr(app.id)) {
                    loginLog.clientId = app.id
                    loginLog.clientName = app.appName
                    loginLog.identify = app.identify
                    getTokenInfo(routeLogMessage.token!!).let {
                        if (it.isNotEmpty()) {
                            loginLog.userId = it.getValue(TokenConstant.USER_INFO_ID)
                            loginLog.loginNo = it.getValue(TokenConstant.USER_INFO_LOGIN_NO)
                            loginLog.userName = it.getValue(TokenConstant.USER_INFO_NAME)
                            loginLog.loginDate = CommonTools.getDateTimeString(Calculation.getCalendar(loginLog.requestTime), Calculation.DATE_FORMAT)
                            loginLogRepository.save(loginLog)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException("登录日志记录失败")
        }
    }

    fun doQueryRouteLog(routeLogQueryPo: RouteLogQueryPo): Page<out Any> =
            routeLogQueryPo.let {
                if (it.history) {
                    routeLogHistoryRepository
                } else {
                    routeLogRepository
                }
            }.let {
                it.findAll({ root, _, criteriaBuilder ->
                    val predicateList: MutableList<Predicate> = mutableListOf()
                    if (!CommonTools.isNullStr(routeLogQueryPo.remoteIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("remoteIp").`as`(String::class.java), "%" + routeLogQueryPo.remoteIp + "%"))
                    }
                    if (!CommonTools.isNullStr(routeLogQueryPo.gatewayIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("gatewayIp").`as`(String::class.java), "%" + routeLogQueryPo.gatewayIp + "%"))
                    }
                    if (!CommonTools.isNullStr(routeLogQueryPo.path)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("path").`as`(String::class.java), "%" + routeLogQueryPo.path + "%"))
                    }
                    if (!CommonTools.isNullStr(routeLogQueryPo.serverId)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("serverId").`as`(String::class.java), "%" + routeLogQueryPo.serverId + "%"))
                    }
                    if (!CommonTools.isNullStr(routeLogQueryPo.clientName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("clientName").`as`(String::class.java), "%" + routeLogQueryPo.clientName + "%"))
                    }
                    if (!CommonTools.isNullStr(routeLogQueryPo.userName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("userName").`as`(String::class.java), "%" + routeLogQueryPo.userName + "%"))
                    }
                    if (routeLogQueryPo.startTime != null) {
                        predicateList.add(criteriaBuilder.ge(root.get<Any>("requestTime").`as`(Long::class.java), routeLogQueryPo.startTime))
                    }
                    if (routeLogQueryPo.endTime != null) {
                        predicateList.add(criteriaBuilder.le(root.get<Any>("requestTime").`as`(Long::class.java), routeLogQueryPo.endTime))
                    }
                    if (routeLogQueryPo.responseStatus != null) {
                        predicateList.add(criteriaBuilder.equal(root.get<Any>("responseStatus").`as`(Long::class.java), routeLogQueryPo.responseStatus))
                    }
                    criteriaBuilder.and(*predicateList.toTypedArray())
                }, buildPageRequest(routeLogQueryPo.queryParam!!))
            }

    fun doQueryOperateLog(operateLogQueryPo: OperateLogQueryPo): Page<out Any> =
            operateLogQueryPo.let {
                if (it.history) {
                    operateLogHistoryRepository
                } else {
                    operateLogRepository
                }
            }.let {
                it.findAll({ root, _, criteriaBuilder ->
                    val predicateList: MutableList<Predicate> = mutableListOf()
                    if (!CommonTools.isNullStr(operateLogQueryPo.remoteIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("remoteIp").`as`(String::class.java), "%" + operateLogQueryPo.remoteIp + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogQueryPo.gatewayIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("gatewayIp").`as`(String::class.java), "%" + operateLogQueryPo.gatewayIp + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogQueryPo.path)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("path").`as`(String::class.java), "%" + operateLogQueryPo.path + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogQueryPo.serverId)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("serverId").`as`(String::class.java), "%" + operateLogQueryPo.serverId + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogQueryPo.clientName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("clientName").`as`(String::class.java), "%" + operateLogQueryPo.clientName + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogQueryPo.userName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("userName").`as`(String::class.java), "%" + operateLogQueryPo.userName + "%"))
                    }
                    if (operateLogQueryPo.startTime != null) {
                        predicateList.add(criteriaBuilder.ge(root.get<Any>("requestTime").`as`(Long::class.java), operateLogQueryPo.startTime))
                    }
                    if (operateLogQueryPo.endTime != null) {
                        predicateList.add(criteriaBuilder.le(root.get<Any>("requestTime").`as`(Long::class.java), operateLogQueryPo.endTime))
                    }
                    criteriaBuilder.and(*predicateList.toTypedArray())
                }, buildPageRequest(operateLogQueryPo.queryParam!!))
            }

    fun doQueryLoginLog(loginLogQueryPo: LoginLogQueryPo): Page<out Any> =
            loginLogQueryPo.let {
                if (it.history) {
                    loginLogHistoryRepository
                } else {
                    loginLogRepository
                }
            }.let {
                it.findAll({ root, _, criteriaBuilder ->
                    val predicateList: MutableList<Predicate> = mutableListOf()
                    if (!CommonTools.isNullStr(loginLogQueryPo.remoteIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("remoteIp").`as`(String::class.java), "%" + loginLogQueryPo.remoteIp + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogQueryPo.gatewayIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("gatewayIp").`as`(String::class.java), "%" + loginLogQueryPo.gatewayIp + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogQueryPo.path)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("path").`as`(String::class.java), "%" + loginLogQueryPo.path + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogQueryPo.serverId)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("serverId").`as`(String::class.java), "%" + loginLogQueryPo.serverId + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogQueryPo.clientName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("clientName").`as`(String::class.java), "%" + loginLogQueryPo.clientName + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogQueryPo.userName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("userName").`as`(String::class.java), "%" + loginLogQueryPo.userName + "%"))
                    }
                    if (loginLogQueryPo.startTime != null) {
                        predicateList.add(criteriaBuilder.ge(root.get<Any>("requestTime").`as`(Long::class.java), loginLogQueryPo.startTime))
                    }
                    if (loginLogQueryPo.endTime != null) {
                        predicateList.add(criteriaBuilder.le(root.get<Any>("requestTime").`as`(Long::class.java), loginLogQueryPo.endTime))
                    }
                    criteriaBuilder.and(*predicateList.toTypedArray())
                }, buildPageRequest(loginLogQueryPo.queryParam!!))
            }

}
