package pers.acp.admin.workflow.listener

import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType
import org.flowable.common.engine.api.delegate.event.FlowableEvent
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl
import org.flowable.engine.HistoryService
import org.flowable.engine.TaskService
import org.flowable.identitylink.api.IdentityLinkInfo
import org.flowable.identitylink.api.IdentityLinkType
import org.flowable.spring.SpringProcessEngineConfiguration
import org.flowable.task.service.impl.persistence.entity.TaskEntity
import org.springframework.stereotype.Component
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.workflow.base.BaseEventListener
import pers.acp.admin.workflow.base.PendingCreatedNotify
import pers.acp.admin.workflow.base.PendingFinishedNotify
import pers.acp.admin.workflow.conf.WorkFlowCustomerConfiguration
import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import java.lang.Exception
import javax.annotation.PostConstruct
import kotlin.jvm.Throws

@Component
class TaskCreateListener
constructor(
    private val logAdapter: LogAdapter,
    private val engineConfiguration: SpringProcessEngineConfiguration,
    private val workFlowCustomerConfiguration: WorkFlowCustomerConfiguration,
    private val pendingCreatedNotifyList: List<PendingCreatedNotify>,
    private val pendingFinishedNotifyList: List<PendingFinishedNotify>,
    private val commonOauthServer: CommonOauthServer,
    private val taskService: TaskService,
    private val historyService: HistoryService
) : BaseEventListener() {
    @PostConstruct
    fun register() {
        engineConfiguration.eventDispatcher.addEventListener(this, FlowableEngineEventType.TASK_CREATED)
    }

    override fun onEvent(event: FlowableEvent?) {
        try {
            if (event is FlowableEntityEventImpl) {
                event.entity.let { entity ->
                    if (entity is TaskEntity) {
                        notifyPendingCreated(entity.id)
                    }
                }
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }
    }

    /**
     * 获取待办人员ID
     */
    private fun getPendingUserIdFromIdentifyLink(identityLinkList: List<IdentityLinkInfo>): List<String> =
        identityLinkList.filter { identityLink ->
            identityLink.type == IdentityLinkType.ASSIGNEE && !CommonTools.isNullStr(identityLink.userId)
        }.map { identityLink -> identityLink.userId }.ifEmpty {
            identityLinkList.filter { identityLink ->
                identityLink.type == IdentityLinkType.CANDIDATE
            }.flatMap { identityLink ->
                mutableListOf<String>().apply {
                    if (!CommonTools.isNullStr(identityLink.userId)) {
                        this.add(identityLink.userId)
                    }
                    if (!CommonTools.isNullStr(identityLink.groupId)) {
                        this.addAll(commonOauthServer.findUserList(identityLink.groupId).map { userVo ->
                            if (!CommonTools.isNullStr(userVo.id)) {
                                userVo.id!!
                            } else {
                                ""
                            }
                        })
                    }
                }
            }
        }.toSet().filter { userId -> !CommonTools.isNullStr(userId) }.toList()

    /**
     * 获取运行时任务下所有待办人员
     * @param taskId 任务ID
     * @return 用户ID
     */
    @Throws(ServerException::class)
    fun findTaskPendingUserIdList(taskId: String): List<String> =
        getPendingUserIdFromIdentifyLink(taskService.getIdentityLinksForTask(taskId))

    /**
     * 获取历史任务下所有待办人员
     * @param taskId 任务ID
     * @return 用户ID
     */
    @Throws(ServerException::class)
    fun findHistoryTaskPendingUserIdList(taskId: String): List<String> =
        getPendingUserIdFromIdentifyLink(historyService.getHistoricIdentityLinksForTask(taskId))

    /**
     * 生成待办通知
     * @param taskId 任务ID
     * @param userIdList 用户ID，默认null；当用户ID为null时，自动取运行时任务所有待办人员
     */
    @Throws(ServerException::class)
    fun notifyPendingCreated(taskId: String, userIdList: List<String>? = null) {
        if (workFlowCustomerConfiguration.notifyPendingCreated) {
            (userIdList ?: findTaskPendingUserIdList(taskId)).apply {
                if (this.isNotEmpty()) {
                    pendingCreatedNotifyList.forEach { it.doTaskNotify(taskId, this) }
                }
            }
        }
    }

    /**
     * 完成待办通知
     * @param taskId 任务ID
     * @param userIdList 用户ID，默认null；当用户ID为null时，自动取任务所有待办人员
     */
    @Throws(ServerException::class)
    fun notifyPendingFinished(taskId: String, userIdList: List<String>? = null) {
        if (workFlowCustomerConfiguration.notifyPendingFinished) {
            (userIdList ?: findHistoryTaskPendingUserIdList(taskId)).apply {
                if (this.isNotEmpty()) {
                    pendingFinishedNotifyList.forEach { it.doTaskNotify(taskId, this) }
                }
            }
        }
    }
}