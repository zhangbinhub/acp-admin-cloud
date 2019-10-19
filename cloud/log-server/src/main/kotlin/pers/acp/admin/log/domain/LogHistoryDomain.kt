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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification

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

    private fun <T> selectLogSpecification(timeBegin: Long): Specification<T> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.and(*mutableListOf(criteriaBuilder.le(root.get<Any>("requestTime").`as`(Long::class.java), timeBegin)).toTypedArray())
        }
    }

    private fun selectLogPageable(quantityPerProcess: Int): Pageable = PageRequest.of(0, quantityPerProcess, Sort.Direction.ASC, "requestTime")

    @Transactional
    fun doRouteLogHistory(timeBegin: Long, quantityPerProcess: Int): Int {
        logAdapter.info("开始执行：路由日志迁移至历史库")
        return routeLogRepository.findAll(selectLogSpecification(timeBegin), selectLogPageable(quantityPerProcess)).let {
            logAdapter.info("本次处理${it.content.size}条路由日志")
            it.forEach { item ->
                routeLogHistoryRepository.save(logEntityToHistoryEntity(item, RouteLogHistory::class.java))
            }
            routeLogRepository.deleteAll(it.content)
            if (it.isEmpty) {
                logAdapter.info("路由日志迁移完成")
            }
            it.content.size
        }
    }

    @Transactional
    fun doOperateLogHistory(timeBegin: Long, quantityPerProcess: Int): Int {
        logAdapter.info("开始执行：操作日志迁移至历史库")
        return operateLogRepository.findAll(selectLogSpecification(timeBegin), selectLogPageable(quantityPerProcess)).let {
            logAdapter.info("本次处理${it.content.size}条操作日志")
            it.forEach { item ->
                operateLogHistoryRepository.save(logEntityToHistoryEntity(item, OperateLogHistory::class.java))
            }
            operateLogRepository.deleteAll(it.content)
            if (it.isEmpty) {
                logAdapter.info("操作日志迁移完成")
            }
            it.content.size
        }
    }

    @Transactional
    fun doLoginLogHistory(timeBegin: Long, quantityPerProcess: Int): Int {
        logAdapter.info("开始执行：登录日志迁移至历史库")
        return loginLogRepository.findAll(selectLogSpecification(timeBegin), selectLogPageable(quantityPerProcess)).let {
            logAdapter.info("本次处理${it.content.size}条登录日志")
            it.forEach { item ->
                loginLogHistoryRepository.save(logEntityToHistoryEntity(item, LoginLogHistory::class.java))
            }
            loginLogRepository.deleteAll(it.content)
            if (it.isEmpty) {
                logAdapter.info("登录日志迁移完成")
            }
            it.content.size
        }
    }

    private fun <T> logEntityToHistoryEntity(srcObj: Any, cls: Class<T>): T =
            objectMapper.readValue(objectMapper.writeValueAsBytes(srcObj), cls)

}