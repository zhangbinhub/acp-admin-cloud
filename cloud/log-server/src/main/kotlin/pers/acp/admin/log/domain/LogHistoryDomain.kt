package pers.acp.admin.log.domain

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.log.entity.LoginLogHistory
import pers.acp.admin.log.entity.OperateLogHistory
import pers.acp.admin.log.entity.RouteLogHistory
import pers.acp.admin.log.repo.*
import pers.acp.spring.boot.interfaces.LogAdapter
import java.lang.Exception

/**
 * @author zhang by 12/09/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class LogHistoryDomain @Autowired
constructor(private val logAdapter: LogAdapter,
            private val objectMapper: ObjectMapper,
            private val routeLogRepository: RouteLogRepository,
            private val operateLogRepository: OperateLogRepository,
            private val loginLogRepository: LoginLogRepository,
            private val routeLogHistoryRepository: RouteLogHistoryRepository,
            private val operateLogHistoryRepository: OperateLogHistoryRepository,
            private val loginLogHistoryRepository: LoginLogHistoryRepository) {

    @Transactional
    fun doRouteLogHistory(timeBegin: Long) {
        logAdapter.info("开始执行：路由日志迁移至历史库")
        routeLogRepository.findAllByRequestTimeLessThan(timeBegin).also {
            logAdapter.info("共处理${it.size}条路由日志")
        }.forEach { item ->
            routeLogHistoryRepository.save(logEntityToHistoryEntity(item, RouteLogHistory::class.java))
        }
        routeLogRepository.deleteAllByRequestTimeLessThan(timeBegin)
        logAdapter.info("路由日志迁移完成")
    }

    @Transactional
    fun doOperateLogHistory(timeBegin: Long) {
        logAdapter.info("开始执行：操作日志迁移至历史库")
        operateLogRepository.findAllByRequestTimeLessThan(timeBegin).also {
            logAdapter.info("共处理${it.size}条操作日志")
        }.forEach { item ->
            operateLogHistoryRepository.save(logEntityToHistoryEntity(item, OperateLogHistory::class.java))
        }
        operateLogRepository.deleteAllByRequestTimeLessThan(timeBegin)
        logAdapter.info("操作日志迁移完成")
    }

    @Transactional
    fun doLoginLogHistory(timeBegin: Long) {
        logAdapter.info("开始执行：登录日志迁移至历史库")
        loginLogRepository.findAllByRequestTimeLessThan(timeBegin).also {
            logAdapter.info("共处理${it.size}条登录日志")
        }.forEach { item ->
            loginLogHistoryRepository.save(logEntityToHistoryEntity(item, LoginLogHistory::class.java))
        }
        loginLogRepository.deleteAllByRequestTimeLessThan(timeBegin)
        logAdapter.info("登录日志迁移完成")
    }

    private fun <T> logEntityToHistoryEntity(srcObj: Any, cls: Class<T>): T =
            objectMapper.readValue(objectMapper.writeValueAsBytes(srcObj), cls)

}