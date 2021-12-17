package pers.acp.admin.log.schedule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.admin.log.conf.LogServerCustomerConfiguration
import pers.acp.admin.log.constant.LogBackUp
import pers.acp.admin.log.domain.LogHistoryDomain
import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.boot.base.BaseSpringBootScheduledAsyncTask
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.cloud.component.CloudTools
import io.github.zhangbinhub.acp.cloud.lock.DistributedLock

/**
 * @author zhang by 12/09/2019
 * @since JDK 11
 */
@Component("LogDailyHistory")
class LogDailyHistory @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val cloudTools: CloudTools,
    private val logServerCustomerConfiguration: LogServerCustomerConfiguration,
    private val distributedLock: DistributedLock,
    private val logHistoryDomain: LogHistoryDomain
) : BaseSpringBootScheduledAsyncTask() {

    init {
        taskName = "日志记录迁移历史库任务"
    }

    override fun beforeExecuteFun(): Boolean =
        distributedLock.getLock(
            LogBackUp.LOG_BACKUP_DISTRIBUTED_LOCK_KEY,
            cloudTools.getServerIp() + ":" + cloudTools.getServerPort(),
            LogBackUp.LOG_BACKUP_DISTRIBUTED_LOCK_TIME_OUT
        )

    override fun executeFun(): Any? {
        /**
         * 迁移T-1之前的数据，保留T-1和T日
         */
        val beginTime = CommonTools.getNowDateTime().minusDays(1).withTimeAtStartOfDay().millis
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    var entityNumber = 1
                    var totalNumber = 0
                    while (entityNumber > 0) {
                        entityNumber = logHistoryDomain.doRouteLogHistory(
                            beginTime,
                            logServerCustomerConfiguration.quantityPerProcess
                        )
                        totalNumber += entityNumber
                    }
                    logAdapter.info(">>>>>>>>>>>>>>>>>>>>>> 路由日志共迁移${totalNumber}条 ===================")
                } catch (e: Exception) {
                    logAdapter.error(">>>>>>>>>>>>>>>>>>>>>> 路由日志迁移异常：${e.message}", e)
                }
            }
            launch(Dispatchers.IO) {
                try {
                    var entityNumber = 1
                    var totalNumber = 0
                    while (entityNumber > 0) {
                        entityNumber = logHistoryDomain.doOperateLogHistory(
                            beginTime,
                            logServerCustomerConfiguration.quantityPerProcess
                        )
                        totalNumber += entityNumber
                    }
                    logAdapter.info(">>>>>>>>>>>>>>>>>>>>>> 操作日志共迁移${totalNumber}条 ===================")
                } catch (e: Exception) {
                    logAdapter.error(">>>>>>>>>>>>>>>>>>>>>> 操作日志迁移异常：${e.message}", e)
                }
            }
            launch(Dispatchers.IO) {
                try {
                    var entityNumber = 1
                    var totalNumber = 0
                    while (entityNumber > 0) {
                        entityNumber = logHistoryDomain.doLoginLogHistory(
                            beginTime,
                            logServerCustomerConfiguration.quantityPerProcess
                        )
                        totalNumber += entityNumber
                    }
                    logAdapter.info(">>>>>>>>>>>>>>>>>>>>>> 登录日志共迁移${totalNumber}条 ===================")
                } catch (e: Exception) {
                    logAdapter.error(">>>>>>>>>>>>>>>>>>>>>> 登录日志迁移异常：${e.message}", e)
                }
            }
            delay(LogBackUp.LOG_BACKUP_DISTRIBUTED_LOCK_TIME_OUT)
        }
        return true
    }

    override fun afterExecuteFun(result: Any) {
        if (logServerCustomerConfiguration.maxHistoryDayNumber > 0)
            doDeleteHistory()
        distributedLock.releaseLock(
            LogBackUp.LOG_BACKUP_DISTRIBUTED_LOCK_KEY, cloudTools.getServerIp() + ":" + cloudTools.getServerPort(),
        )
    }

    private fun doDeleteHistory() {
        logAdapter.info("开始清理路由、操作、登录日志，最大保留天数：" + logServerCustomerConfiguration.maxHistoryDayNumber)
        runBlocking {
            val time = CommonTools.getNowDateTime().withTimeAtStartOfDay()
                .minusDays(logServerCustomerConfiguration.maxHistoryDayNumber).millis
            launch(Dispatchers.IO) {
                logHistoryDomain.doDeleteRouteLogHistory(time)
            }
            launch(Dispatchers.IO) {
                logHistoryDomain.doDeleteOperateLogHistory(time)
            }
            launch(Dispatchers.IO) {
                logHistoryDomain.doDeleteLoginLogHistory(time)
            }
        }
    }
}