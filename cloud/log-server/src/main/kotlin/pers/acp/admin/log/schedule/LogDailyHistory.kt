package pers.acp.admin.log.schedule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.admin.log.conf.LogServerCustomerConfiguration
import pers.acp.admin.log.constant.LogBackUp
import pers.acp.admin.log.domain.LogHistoryDomain
import pers.acp.core.CommonTools
import pers.acp.spring.boot.base.BaseSpringBootScheduledAsyncTask
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.cloud.lock.DistributedLock

/**
 * @author zhang by 12/09/2019
 * @since JDK 11
 */
@Component("LogDailyHistory")
class LogDailyHistory @Autowired
constructor(private val logAdapter: LogAdapter,
            private val logServerCustomerConfiguration: LogServerCustomerConfiguration,
            private val distributedLock: DistributedLock,
            private val logHistoryDomain: LogHistoryDomain) : BaseSpringBootScheduledAsyncTask() {

    init {
        taskName = "日志记录迁移历史库任务"
    }

    override fun beforeExecuteFun(): Boolean =
            distributedLock.getLock(LogBackUp.LOG_BACKUP_DISTRIBUTED_LOCK_KEY,
                    logServerCustomerConfiguration.serverIp + ":" + logServerCustomerConfiguration.serverPort,
                    30 * 60 * 1000)

    override fun executeFun(): Any? {
        val todayBeginTime = CommonTools.getNowDateTime().withTimeAtStartOfDay().millis
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    logHistoryDomain.doRouteLogHistory(todayBeginTime)
                } catch (e: Exception) {
                    logAdapter.error("路由日志迁移异常：${e.message}", e)
                }
            }
            launch(Dispatchers.IO) {
                try {
                    logHistoryDomain.doOperateLogHistory(todayBeginTime)
                } catch (e: Exception) {
                    logAdapter.error("操作日志迁移异常：${e.message}", e)
                }
            }
            launch(Dispatchers.IO) {
                try {
                    logHistoryDomain.doLoginLogHistory(todayBeginTime)
                } catch (e: Exception) {
                    logAdapter.error("登录日志迁移异常：${e.message}", e)
                }
            }
        }
        println("并行任务全部执行完成")
        return true
    }

    override fun afterExecuteFun(result: Any) {
        distributedLock.releaseLock(LogBackUp.LOG_BACKUP_DISTRIBUTED_LOCK_KEY,
                logServerCustomerConfiguration.serverIp + ":" + logServerCustomerConfiguration.serverPort)
    }
}