package pers.acp.admin.log.domain

import org.springframework.beans.BeanUtils
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
import pers.acp.admin.log.constant.LogConstant
import pers.acp.core.CommonTools

/**
 * @author zhang by 12/09/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class LogHistoryDomain @Autowired
constructor(private val logAdapter: LogAdapter,
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
                RouteLogHistory().apply {
                    BeanUtils.copyProperties(item, this)
                }.apply {
                    routeLogHistoryRepository.save(this)
                }
            }
            routeLogRepository.deleteAll(it.content)
            if (it.isEmpty) {
                logAdapter.info("路由日志迁移完成")
            }
            it.content.size
        }
    }

    @Transactional
    fun doDeleteRouteLogHistory(time: Long) {
        logAdapter.info("开始清理历史路由日志...")
        routeLogHistoryRepository.deleteAllByRequestTimeLessThan(time)
        logAdapter.info("历史路由日志清理完成")
    }

    @Transactional
    fun doOperateLogHistory(timeBegin: Long, quantityPerProcess: Int): Int {
        logAdapter.info("开始执行：操作日志迁移至历史库")
        return operateLogRepository.findAll(selectLogSpecification(timeBegin), selectLogPageable(quantityPerProcess)).let {
            logAdapter.info("本次处理${it.content.size}条操作日志")
            it.forEach { item ->
                OperateLogHistory().apply {
                    BeanUtils.copyProperties(item, this)
                }.apply {
                    operateLogHistoryRepository.save(this)
                }
            }
            operateLogRepository.deleteAll(it.content)
            if (it.isEmpty) {
                logAdapter.info("操作日志迁移完成")
            }
            it.content.size
        }
    }

    @Transactional
    fun doDeleteOperateLogHistory(time: Long) {
        logAdapter.info("开始清理历史操作日志...")
        operateLogHistoryRepository.deleteAllByRequestTimeLessThan(time)
        logAdapter.info("历史操作日志清理完成")
    }

    @Transactional
    fun doLoginLogHistory(timeBegin: Long, quantityPerProcess: Int): Int {
        logAdapter.info("开始执行：登录日志迁移至历史库")
        return loginLogRepository.findAll(selectLogSpecification(timeBegin), selectLogPageable(quantityPerProcess)).let {
            logAdapter.info("本次处理${it.content.size}条登录日志")
            it.forEach { item ->
                LoginLogHistory().apply {
                    BeanUtils.copyProperties(item, this)
                }.apply {
                    loginLogHistoryRepository.save(this)
                }
            }
            loginLogRepository.deleteAll(it.content)
            if (it.isEmpty) {
                logAdapter.info("登录日志迁移完成")
            }
            it.content.size
        }
    }

    @Transactional
    fun doDeleteLoginLogHistory(time: Long) {
        logAdapter.info("开始清理历史登录日志...")
        CommonTools.getNowDateTime().withTimeAtStartOfDay().minusMonths(LogConstant.LOGIN_LOG_STATISTICS_MAX_MONTH).millis.also {
            if (it < time) {
                loginLogHistoryRepository.deleteAllByRequestTimeLessThan(it)
            } else {
                loginLogHistoryRepository.deleteAllByRequestTimeLessThan(time)
            }
        }
        logAdapter.info("历史登录日志清理完成")
    }
}