package pers.acp.admin.deploy.bus.listener

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import pers.acp.admin.common.event.ExecuteBusEvent
import pers.acp.admin.deploy.constant.DeployConstant
import pers.acp.admin.deploy.domain.DeployTaskDomain
import io.github.zhangbinhub.acp.core.task.BaseAsyncTask
import io.github.zhangbinhub.acp.core.task.threadpool.ThreadPoolService
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.cloud.component.CloudTools
import io.github.zhangbinhub.acp.cloud.lock.DistributedLock

@Component
class DeployExecuteEventListener @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val cloudTools: CloudTools,
    private val distributedLock: DistributedLock,
    private val objectMapper: ObjectMapper,
    private val deployTaskDomain: DeployTaskDomain
) : ApplicationListener<ExecuteBusEvent> {
    override fun onApplicationEvent(executeBusEvent: ExecuteBusEvent) {
        if (executeBusEvent.message == DeployConstant.busMessageExecuteDeploy) {
            logAdapter.info("收到执行部署任务事件")
            try {
                logAdapter.debug(objectMapper.writeValueAsString(executeBusEvent))
                if (executeBusEvent.paramList.isNotEmpty()) {
                    ThreadPoolService.getInstance(1, 1, Int.MAX_VALUE, DeployConstant.busMessageExecuteDeploy)
                        .addTask(object : BaseAsyncTask(DeployConstant.busMessageExecuteDeploy, false) {
                            override fun beforeExecuteFun(): Boolean = true
                            override fun executeFun(): Any? {
                                val deployTaskId = executeBusEvent.paramList.first()
                                try {
                                    if (getLock()) {
                                        try {
                                            logAdapter.info("开始执行部署任务...")
                                            runBlocking {
                                                deployTaskDomain.doExecuteTask(deployTaskId)
                                            }
                                            logAdapter.info("部署任务执行完成！")
                                        } finally {
                                            releaseLock()
                                        }
                                    }
                                } catch (e: Exception) {
                                    logAdapter.error(e.message, e)
                                }
                                return true
                            }

                            override fun afterExecuteFun(result: Any) {}
                        })
                    runBlocking {
                        delay(DeployConstant.DISTRIBUTED_LOCK_TIME_OUT)
                    }
                }
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
            }
        }
    }

    private fun getLock(): Boolean =
        distributedLock.getLock(
            DeployConstant.DEPLOY_EXECUTE_DISTRIBUTED_LOCK_PREFIX + cloudTools.getServerIp(),
            cloudTools.getServerIp() + ":" + cloudTools.getServerPort(),
            DeployConstant.DISTRIBUTED_LOCK_TIME_OUT
        )

    private fun releaseLock() {
        runBlocking {
            delay(DeployConstant.DISTRIBUTED_LOCK_TIME_OUT)
        }
        distributedLock.releaseLock(
            DeployConstant.DEPLOY_EXECUTE_DISTRIBUTED_LOCK_PREFIX + cloudTools.getServerIp(),
            cloudTools.getServerIp() + ":" + cloudTools.getServerPort()
        )
    }
}