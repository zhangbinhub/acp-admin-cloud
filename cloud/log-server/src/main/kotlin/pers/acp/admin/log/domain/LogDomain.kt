package pers.acp.admin.log.domain

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.common.vo.ApplicationVo
import pers.acp.admin.constant.TokenConstant
import pers.acp.admin.log.base.BaseLogEntity
import pers.acp.admin.log.constant.LogConstant
import pers.acp.admin.log.entity.LoginLog
import pers.acp.admin.log.entity.OperateLog
import pers.acp.admin.log.entity.RouteLog
import pers.acp.admin.log.message.RouteLogMessage
import pers.acp.admin.log.po.LogQueryPo
import pers.acp.admin.log.repo.*
import pers.acp.admin.log.vo.LoginLogVo
import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.core.task.timer.Calculation
import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import javax.persistence.criteria.Predicate

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class LogDomain @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val objectMapper: ObjectMapper,
    private val routeLogRepository: RouteLogRepository,
    private val routeLogHistoryRepository: RouteLogHistoryRepository,
    private val operateLogRepository: OperateLogRepository,
    private val operateLogHistoryRepository: OperateLogHistoryRepository,
    private val loginLogRepository: LoginLogRepository,
    private val loginLogHistoryRepository: LoginLogHistoryRepository,
    private val commonOauthServer: CommonOauthServer
) : BaseDomain() {

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
                    this[TokenConstant.USER_INFO_ID] =
                        oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_ID]!!.toString()
                    this[TokenConstant.USER_INFO_LOGIN_NO] =
                        oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_LOGIN_NO]!!.toString()
                    this[TokenConstant.USER_INFO_NAME] =
                        oAuth2AccessToken.additionalInformation[TokenConstant.USER_INFO_NAME]!!.toString()
                }
            } else {
                mapOf()
            }
        } ?: mapOf()

    private fun getAppInfo(token: String): ApplicationVo = commonOauthServer.appInfo(token)

    @Transactional
    fun doRouteLog(routeLogMessage: RouteLogMessage, message: String) {
        val routeLog = messageToEntity(message, RouteLog::class.java)
        if (routeLogMessage.token != null) {
            try {
                getAppInfo(routeLogMessage.token!!).let { app ->
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
                runBlocking {
                    delay(LogConstant.ROUTE_LOG_QUERY_INTERVAL_TIME)
                }
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
    fun doOperateLog(routeLogMessage: RouteLogMessage, message: String) {
        try {
            val operateLog = messageToEntity(message, OperateLog::class.java)
            getAppInfo(routeLogMessage.token!!).let { app ->
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
            getAppInfo(routeLogMessage.token!!).let { app ->
                if (!CommonTools.isNullStr(app.id)) {
                    loginLog.clientId = app.id
                    loginLog.clientName = app.appName
                    loginLog.identify = app.identify
                    getTokenInfo(routeLogMessage.token!!).let {
                        if (it.isNotEmpty()) {
                            loginLog.userId = it.getValue(TokenConstant.USER_INFO_ID)
                            loginLog.loginNo = it.getValue(TokenConstant.USER_INFO_LOGIN_NO)
                            loginLog.userName = it.getValue(TokenConstant.USER_INFO_NAME)
                            loginLog.loginDate = CommonTools.getDateTimeString(
                                Calculation.getCalendar(loginLog.requestTime),
                                Calculation.DATE_FORMAT
                            )
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

    fun loginStatistics(beginTime: Long): List<LoginLogVo> =
        loginLogHistoryRepository.loginStatistics(beginTime).let {
            it.addAll(loginLogRepository.loginStatistics())
            it.map { item ->
                LoginLogVo(item[0].toString(), item[1].toString(), item[2].toString(), item[3].toString().toLong())
            }
        }

    fun doPageQuery(
        baseRepository: BaseRepository<out BaseLogEntity, String>,
        logQueryPo: LogQueryPo
    ): Page<out BaseLogEntity> =
        baseRepository.findAll({ root, _, criteriaBuilder ->
            val predicateList: MutableList<Predicate> = mutableListOf()
            if (!CommonTools.isNullStr(logQueryPo.remoteIp)) {
                predicateList.add(
                    criteriaBuilder.like(
                        root.get<Any>("remoteIp").`as`(String::class.java),
                        "%" + logQueryPo.remoteIp + "%"
                    )
                )
            }
            if (!CommonTools.isNullStr(logQueryPo.gatewayIp)) {
                predicateList.add(
                    criteriaBuilder.like(
                        root.get<Any>("gatewayIp").`as`(String::class.java),
                        "%" + logQueryPo.gatewayIp + "%"
                    )
                )
            }
            if (!CommonTools.isNullStr(logQueryPo.path)) {
                predicateList.add(
                    criteriaBuilder.like(
                        root.get<Any>("path").`as`(String::class.java),
                        "%" + logQueryPo.path + "%"
                    )
                )
            }
            if (!CommonTools.isNullStr(logQueryPo.serverId)) {
                predicateList.add(
                    criteriaBuilder.like(
                        root.get<Any>("serverId").`as`(String::class.java),
                        "%" + logQueryPo.serverId + "%"
                    )
                )
            }
            if (!CommonTools.isNullStr(logQueryPo.clientName)) {
                predicateList.add(
                    criteriaBuilder.like(
                        root.get<Any>("clientName").`as`(String::class.java),
                        "%" + logQueryPo.clientName + "%"
                    )
                )
            }
            if (!CommonTools.isNullStr(logQueryPo.userName)) {
                predicateList.add(
                    criteriaBuilder.like(
                        root.get<Any>("userName").`as`(String::class.java),
                        "%" + logQueryPo.userName + "%"
                    )
                )
            }
            if (logQueryPo.startTime != null) {
                predicateList.add(
                    criteriaBuilder.ge(
                        root.get<Any>("requestTime").`as`(Long::class.java),
                        logQueryPo.startTime
                    )
                )
            }
            if (logQueryPo.endTime != null) {
                predicateList.add(
                    criteriaBuilder.le(
                        root.get<Any>("requestTime").`as`(Long::class.java),
                        logQueryPo.endTime
                    )
                )
            }
            if (logQueryPo.responseStatus != null) {
                predicateList.add(
                    criteriaBuilder.equal(
                        root.get<Any>("responseStatus").`as`(Long::class.java),
                        logQueryPo.responseStatus
                    )
                )
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }, buildPageRequest(logQueryPo.queryParam!!))

    fun doQueryRouteLog(logQueryPo: LogQueryPo): Page<out BaseLogEntity> =
        logQueryPo.let {
            if (it.history) {
                routeLogHistoryRepository
            } else {
                routeLogRepository
            }
        }.let {
            doPageQuery(it, logQueryPo)
        }

    fun doQueryOperateLog(logQueryPo: LogQueryPo): Page<out BaseLogEntity> =
        logQueryPo.let {
            if (it.history) {
                operateLogHistoryRepository
            } else {
                operateLogRepository
            }
        }.let {
            doPageQuery(it, logQueryPo)
        }

    fun doQueryLoginLog(logQueryPo: LogQueryPo): Page<out BaseLogEntity> =
        logQueryPo.let {
            if (it.history) {
                loginLogHistoryRepository
            } else {
                loginLogRepository
            }
        }.let {
            doPageQuery(it, logQueryPo)
        }

}
