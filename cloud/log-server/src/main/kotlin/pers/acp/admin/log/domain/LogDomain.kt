package pers.acp.admin.log.domain

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.constant.TokenConstant
import pers.acp.admin.log.entity.LoginLog
import pers.acp.admin.log.entity.OperateLog
import pers.acp.admin.log.entity.RouteLog
import pers.acp.admin.log.feign.OauthServer
import pers.acp.admin.log.message.RouteLogMessage
import pers.acp.admin.log.po.LoginLogPo
import pers.acp.admin.log.po.OperateLogPo
import pers.acp.admin.log.po.RouteLogPo
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
            private val oauthServer: OauthServer) : BaseDomain() {

    /**
     * 路由消息转为对应的实体对象
     */
    private fun <T> messageToEntity(message: String, cls: Class<T>): T {
        return objectMapper.readValue(message, cls) ?: throw ServerException("日志消息转换失败")
    }

    private fun getTokenInfo(token: String): Map<String, String> =
            oauthServer.tokenInfo(token)?.let { oAuth2AccessToken ->
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
                oauthServer.appInfo(routeLogMessage.token!!).let { app ->
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
            while (optionalRouteLog.isEmpty) {
                delay(1000)
                optionalRouteLog = routeLogRepository.findByLogIdAndRequestTime(routeLog.logId, routeLog.requestTime)
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
            oauthServer.appInfo(routeLogMessage.token!!).let { app ->
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
            oauthServer.appInfo(routeLogMessage.token!!).let { app ->
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

    fun doQueryRouteLog(routeLogPo: RouteLogPo): Page<out Any> =
            routeLogPo.let {
                if (it.history) {
                    routeLogHistoryRepository
                } else {
                    routeLogRepository
                }
            }.let {
                it.findAll({ root, _, criteriaBuilder ->
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
                    if (!CommonTools.isNullStr(routeLogPo.clientName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("clientName").`as`(String::class.java), "%" + routeLogPo.clientName + "%"))
                    }
                    if (!CommonTools.isNullStr(routeLogPo.userName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("userName").`as`(String::class.java), "%" + routeLogPo.userName + "%"))
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

    fun doQueryOperateLog(operateLogPo: OperateLogPo): Page<out Any> =
            operateLogPo.let {
                if (it.history) {
                    operateLogHistoryRepository
                } else {
                    operateLogRepository
                }
            }.let {
                it.findAll({ root, _, criteriaBuilder ->
                    val predicateList: MutableList<Predicate> = mutableListOf()
                    if (!CommonTools.isNullStr(operateLogPo.remoteIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("remoteIp").`as`(String::class.java), "%" + operateLogPo.remoteIp + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogPo.gatewayIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("gatewayIp").`as`(String::class.java), "%" + operateLogPo.gatewayIp + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogPo.path)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("path").`as`(String::class.java), "%" + operateLogPo.path + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogPo.serverId)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("serverId").`as`(String::class.java), "%" + operateLogPo.serverId + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogPo.clientName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("clientName").`as`(String::class.java), "%" + operateLogPo.clientName + "%"))
                    }
                    if (!CommonTools.isNullStr(operateLogPo.userName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("userName").`as`(String::class.java), "%" + operateLogPo.userName + "%"))
                    }
                    if (operateLogPo.startTime != null) {
                        predicateList.add(criteriaBuilder.ge(root.get<Any>("requestTime").`as`(Long::class.java), operateLogPo.startTime))
                    }
                    if (operateLogPo.endTime != null) {
                        predicateList.add(criteriaBuilder.le(root.get<Any>("requestTime").`as`(Long::class.java), operateLogPo.endTime))
                    }
                    criteriaBuilder.and(*predicateList.toTypedArray())
                }, buildPageRequest(operateLogPo.queryParam!!))
            }

    fun doQueryLoginLog(loginLogPo: LoginLogPo): Page<out Any> =
            loginLogPo.let {
                if (it.history) {
                    loginLogHistoryRepository
                } else {
                    loginLogRepository
                }
            }.let {
                it.findAll({ root, _, criteriaBuilder ->
                    val predicateList: MutableList<Predicate> = mutableListOf()
                    if (!CommonTools.isNullStr(loginLogPo.remoteIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("remoteIp").`as`(String::class.java), "%" + loginLogPo.remoteIp + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogPo.gatewayIp)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("gatewayIp").`as`(String::class.java), "%" + loginLogPo.gatewayIp + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogPo.path)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("path").`as`(String::class.java), "%" + loginLogPo.path + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogPo.serverId)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("serverId").`as`(String::class.java), "%" + loginLogPo.serverId + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogPo.clientName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("clientName").`as`(String::class.java), "%" + loginLogPo.clientName + "%"))
                    }
                    if (!CommonTools.isNullStr(loginLogPo.userName)) {
                        predicateList.add(criteriaBuilder.like(root.get<Any>("userName").`as`(String::class.java), "%" + loginLogPo.userName + "%"))
                    }
                    if (loginLogPo.startTime != null) {
                        predicateList.add(criteriaBuilder.ge(root.get<Any>("requestTime").`as`(Long::class.java), loginLogPo.startTime))
                    }
                    if (loginLogPo.endTime != null) {
                        predicateList.add(criteriaBuilder.le(root.get<Any>("requestTime").`as`(Long::class.java), loginLogPo.endTime))
                    }
                    criteriaBuilder.and(*predicateList.toTypedArray())
                }, buildPageRequest(loginLogPo.queryParam!!))
            }

}
