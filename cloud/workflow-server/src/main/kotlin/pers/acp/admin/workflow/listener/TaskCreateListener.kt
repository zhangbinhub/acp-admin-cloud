package pers.acp.admin.workflow.listener

import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType
import org.flowable.common.engine.api.delegate.event.FlowableEvent
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl
import org.flowable.engine.TaskService
import org.flowable.identitylink.api.IdentityLinkType
import org.flowable.spring.SpringProcessEngineConfiguration
import org.flowable.task.service.impl.persistence.entity.TaskEntity
import org.springframework.stereotype.Component
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.workflow.base.BaseEventListener
import pers.acp.admin.workflow.base.PendingCreatedNotify
import pers.acp.admin.workflow.base.PendingFinishedNotify
import pers.acp.admin.workflow.conf.WorkFlowCustomerConfiguration
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
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
    private val taskService: TaskService
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
                        findTaskPendingUserIdList(entity.id).forEach { userId ->
                            notifyPendingCreated(entity.id, userId)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }
    }

    @Throws(ServerException::class)
    fun findTaskPendingUserIdList(taskId: String): List<String> =
        taskService.getIdentityLinksForTask(taskId).let { identityLinkList ->
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
            }
        }.toSet().filter { userId -> !CommonTools.isNullStr(userId) }.toList()

    @Throws(ServerException::class)
    fun notifyPendingCreated(taskId: String, userId: String) {
        if (workFlowCustomerConfiguration.notifyPendingCreated) {
            pendingCreatedNotifyList.forEach { it.doNotify(taskId, userId) }
        }
    }

    @Throws(ServerException::class)
    fun notifyPendingFinished(taskId: String, userId: String) {
        if (workFlowCustomerConfiguration.notifyPendingFinished) {
            pendingFinishedNotifyList.forEach { it.doNotify(taskId, userId) }
        }
    }
}