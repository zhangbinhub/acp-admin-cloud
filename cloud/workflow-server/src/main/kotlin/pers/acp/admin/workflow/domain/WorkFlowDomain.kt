package pers.acp.admin.workflow.domain

import org.flowable.bpmn.constants.BpmnXMLConstants
import org.flowable.bpmn.model.FlowElement
import org.flowable.bpmn.model.FlowNode
import org.flowable.common.engine.impl.identity.Authentication
import org.flowable.engine.*
import org.flowable.engine.history.HistoricActivityInstance
import org.flowable.engine.history.HistoricProcessInstance
import org.flowable.engine.history.HistoricVariableUpdate
import org.flowable.engine.runtime.ProcessInstance
import org.flowable.task.api.DelegationState
import org.flowable.task.api.Task
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.common.po.*
import pers.acp.admin.common.vo.*
import pers.acp.admin.workflow.base.BaseWorkFlowDomain
import pers.acp.admin.workflow.constant.WorkFlowParamKey
import pers.acp.core.CommonTools
import pers.acp.admin.workflow.entity.MyProcessInstance
import pers.acp.admin.workflow.listener.TaskCreateListener
import pers.acp.admin.workflow.repo.MyProcessInstanceRepository
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import java.io.InputStream
import javax.persistence.criteria.Predicate

/**
 * @author zhang by 10/06/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class WorkFlowDomain @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val taskCreateListener: TaskCreateListener,
    private val commonOauthServer: CommonOauthServer,
    private val formService: FormService,
    private val runtimeService: RuntimeService,
    private val taskService: TaskService,
    private val repositoryService: RepositoryService,
    private val historyService: HistoryService,
    @param:Qualifier("processEngine") private val processEngine: ProcessEngine,
    private val myProcessInstanceRepository: MyProcessInstanceRepository,
) : BaseWorkFlowDomain() {

    private fun getUserById(id: String?): UserVo =
        if (CommonTools.isNullStr(id) || id == "anonymousUser") {
            UserVo()
        } else {
            commonOauthServer.findUserById(id!!)
        }

    private fun getUserListByIdList(idList: List<String>): MutableList<UserVo> =
        commonOauthServer.findUserList(idList).toMutableList()

    /**
     * 任务实体转换
     *
     * @param task 任务对象
     * @return 转换后任务对象
     */
    @Throws(ServerException::class)
    private fun taskToVo(task: Task) =
        runtimeService.createProcessInstanceQuery().processInstanceId(task.processInstanceId).singleResult()
            ?.let { processInstance ->
                val params = runtimeService.getVariables(task.executionId)
                ProcessTaskVo(
                    processInstanceId = task.processInstanceId,
                    name = task.name,
                    taskId = task.id,
                    taskDefinitionKey = task.taskDefinitionKey,
                    parentTaskId = task.parentTaskId,
                    executionId = task.executionId,
                    params = params,
                    businessKey = params[WorkFlowParamKey.businessKey]?.toString() ?: "",
                    unClaimed = task.assignee == null,
                    user = getUserById(task.assignee),
                    localParams = task.taskLocalVariables,
                    properties = formService.getTaskFormData(task.id).formProperties.associateBy(
                        { it.name },
                        { it.value }).toMutableMap(),
                    createTime = task.createTime.time,
                    claimTime = task.claimTime?.time,
                    processDefinitionKey = processInstance.processDefinitionKey,
                    flowName = processInstance.processDefinitionName,
                    title = params[WorkFlowParamKey.title]?.toString() ?: "",
                    description = params[WorkFlowParamKey.description]?.toString() ?: "",
                    startUser = getUserById(processInstance.startUserId),
                    taskOwnerUser = getUserById(task.owner),
                    delegated = task.delegationState == DelegationState.PENDING
                )
            } ?: throw ServerException("获取流程实例失败")

    /**
     * 历史记录实例转换
     *
     * @param historicActivityInstance 历史记录
     * @return 转换后对象
     */
    private fun actToVo(
        historicActivityInstance: HistoricActivityInstance,
        businessKey: String
    ): ProcessHistoryActivityVo =
        historyService.createHistoricDetailQuery().activityInstanceId(historicActivityInstance.id).variableUpdates()
            .orderByTime().asc().list()
            .let { historicDetailList ->
                val params: MutableMap<String, Any> = mutableMapOf()
                val localParams: MutableMap<String, Any> = mutableMapOf()
                historicDetailList.forEach { historicDetail ->
                    val name = (historicDetail as HistoricVariableUpdate).variableName
                    if (CommonTools.isNullStr(historicDetail.taskId)) {
                        if (params.containsKey(name) && WorkFlowParamKey.comment == name) {
                            params[name] = "${params[name].toString()}\n------\n${historicDetail.value}"
                        } else {
                            params[name] = historicDetail.value
                        }
                    } else {
                        if (historicDetail.taskId == historicActivityInstance.taskId) {
                            localParams[name] = historicDetail.value
                        }
                    }
                }
                historyService.createHistoricTaskInstanceQuery().taskId(historicActivityInstance.taskId).singleResult()
                    ?.let {
                        ProcessHistoryActivityVo(
                            processInstanceId = historicActivityInstance.processInstanceId,
                            activityId = historicActivityInstance.activityId,
                            activityName = historicActivityInstance.activityName,
                            taskId = historicActivityInstance.taskId,
                            taskDefinitionKey = it.taskDefinitionKey,
                            executionId = historicActivityInstance.executionId,
                            businessKey = businessKey,
                            user = getUserById(historicActivityInstance.assignee),
                            pass = params[WorkFlowParamKey.pass]?.let { pass -> pass as Boolean },
                            comment = params[WorkFlowParamKey.comment]?.toString(),
                            params = params,
                            localParams = localParams,
                            startTime = historicActivityInstance.startTime.time,
                            endTime = historicActivityInstance.endTime?.time
                        )
                    } ?: throw ServerException("获取任务信息失败！")
            }

    /**
     * 流程实例转换
     * @param processInstance 流程实例
     * @return 转换后的对象
     */
    @Throws(ServerException::class)
    private fun instanceToVo(processInstance: Any): ProcessInstanceVo =
        when (processInstance) {
            is ProcessInstance -> {
                val params = mutableMapOf<String, Any>().apply {
                    runtimeService.createExecutionQuery().processInstanceId(processInstance.id).list()
                        .map { execution -> execution.id }.toSet().let { executionIds ->
                            runtimeService.getVariableInstancesByExecutionIds(executionIds)
                                .forEach { variableInstance ->
                                    this[variableInstance.name] = variableInstance.value
                                }
                        }
                }
                ProcessInstanceVo(
                    processInstanceId = processInstance.id,
                    finished = false,
                    processDefinitionKey = processInstance.processDefinitionKey,
                    businessKey = processInstance.businessKey,
                    flowName = processInstance.processDefinitionName,
                    title = params[WorkFlowParamKey.title]?.toString() ?: "",
                    description = params[WorkFlowParamKey.description]?.toString() ?: "",
                    startUser = getUserById(processInstance.startUserId),
                    activityUser = taskService.createTaskQuery().processInstanceId(processInstance.id).list()
                        .filter { task ->
                            task.assignee != null
                        }.map { task ->
                            task.assignee
                        }.let {
                            if (it.isNotEmpty()) {
                                getUserListByIdList(it)
                            } else {
                                mutableListOf()
                            }
                        },
                    params = params,
                    startTime = processInstance.startTime!!.time
                )
            }
            is HistoricProcessInstance -> {
                val params = mutableMapOf<String, Any>().apply {
                    historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.id).list()
                        .forEach { variableInstance ->
                            this[variableInstance.variableName] = variableInstance.value
                        }
                }
                ProcessInstanceVo(
                    processInstanceId = processInstance.id,
                    finished = true,
                    processDefinitionKey = processInstance.processDefinitionKey,
                    businessKey = processInstance.businessKey,
                    flowName = processInstance.processDefinitionName,
                    title = params[WorkFlowParamKey.title]?.toString() ?: "",
                    description = params[WorkFlowParamKey.description]?.toString() ?: "",
                    startUser = getUserById(processInstance.startUserId),
                    params = params,
                    startTime = processInstance.startTime!!.time,
                    endTime = processInstance.endTime?.time,
                    deleteReason = processInstance.deleteReason
                )
            }
            else -> {
                throw ServerException("流程实例对象转换失败")
            }
        }

    /**
     * 启动流程
     *
     * @param processStartPo 流程启动参数
     * @param userId 流程发起人ID
     * @return 流程实例id
     */
    @Transactional
    @Throws(ServerException::class)
    fun startFlow(processStartPo: ProcessStartPo, userId: String? = null): String =
        try {
            val params = processStartPo.params
            repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processStartPo.processDefinitionKey)
                .orderByProcessDefinitionVersion().desc().list().apply {
                    if (this.isEmpty()) {
                        throw ServerException("找不到对应的流程定义【${processStartPo.processDefinitionKey}】")
                    }
                    params[WorkFlowParamKey.flowName] = this[0].name
                }
            params[WorkFlowParamKey.businessKey] = processStartPo.businessKey!!
            params[WorkFlowParamKey.title] = processStartPo.title!!
            params[WorkFlowParamKey.description] = processStartPo.description!!
            userId?.apply {
                params[WorkFlowParamKey.startUserId] = this
                Authentication.setAuthenticatedUserId(this)
            }
            runtimeService.startProcessInstanceByKey(
                processStartPo.processDefinitionKey,
                processStartPo.businessKey,
                params
            ).id
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 获取任务信息
     *
     * @param taskId 任务ID
     * @return 任务信息
     */
    @Throws(ServerException::class)
    fun findTaskById(taskId: String): ProcessTaskVo =
        try {
            commonOauthServer.userInfo()?.let { userInfo ->
                taskService.createTaskQuery().or()
                    .taskCandidateGroupIn(userInfo.roleSet.map { role -> role.code }.toList())
                    .taskCandidateOrAssigned(userInfo.id).endOr()
                    .taskId(taskId).singleResult()?.let {
                        taskToVo(it)
                    } ?: throw ServerException("找不到信息")
            } ?: throw ServerException("获取当前登录用户信息失败！")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 获取任务信息（开放的）
     *
     * @param taskId 任务ID
     * @return 任务信息
     */
    @Throws(ServerException::class)
    fun findTaskByIdOpen(taskId: String): ProcessTaskVo =
        try {
            taskService.createTaskQuery().taskId(taskId).singleResult()?.let {
                taskToVo(it)
            } ?: throw ServerException("找不到信息")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 获取待办任务
     * @param processInstanceId 流程实例ID
     * @param userId 用户ID
     */
    @Throws(ServerException::class)
    fun findTaskList(processInstanceId: String, userId: String): List<ProcessTaskVo> =
        try {
            taskService.createTaskQuery().processInstanceId(processInstanceId)
                .taskAssignee(userId).list().map {
                    taskToVo(it)
                }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 获取待办任务
     *
     * @return 任务列表
     */
    @Throws(ServerException::class)
    fun findTaskList(processQueryPo: ProcessQueryPo): CustomerQueryPageVo<ProcessTaskVo> =
        try {
            val firstResult = (processQueryPo.queryParam!!.currPage!! - 1) * processQueryPo.queryParam!!.pageSize!!
            val maxResult = processQueryPo.queryParam!!.pageSize!!
            commonOauthServer.userInfo()?.let { userInfo ->
                val taskQuery = taskService.createTaskQuery().or()
                    .taskCandidateGroupIn(userInfo.roleSet.map { role -> role.code }.toList())
                    .taskCandidateOrAssigned(userInfo.id).endOr()
                if (processQueryPo.processDefinitionKeys != null && processQueryPo.processDefinitionKeys!!.isNotEmpty()) {
                    taskQuery.processDefinitionKeyIn(processQueryPo.processDefinitionKeys!!)
                }
                if (processQueryPo.processInstanceIds != null && processQueryPo.processInstanceIds!!.isNotEmpty()) {
                    taskQuery.processInstanceIdIn(processQueryPo.processInstanceIds!!)
                }
                if (!CommonTools.isNullStr(processQueryPo.processBusinessKey)) {
                    taskQuery.processInstanceBusinessKeyLike(processQueryPo.processBusinessKey)
                }
                if (processQueryPo.startTime != null) {
                    taskQuery.taskCreatedAfter(DateTime(processQueryPo.startTime!!).toDate())
                }
                if (processQueryPo.endTime != null) {
                    taskQuery.taskCreatedBefore(DateTime(processQueryPo.endTime!!).toDate())
                }
                val total = taskQuery.list().count()
                val list = taskQuery.orderByTaskCreateTime().asc()
                    .listPage(firstResult, maxResult)
                    .map { task -> taskToVo(task) }
                CustomerQueryPageVo(
                    currPage = processQueryPo.queryParam!!.currPage!!.toLong(),
                    pageSize = processQueryPo.queryParam!!.pageSize!!.toLong(),
                    totalElements = total.toLong(),
                    content = list
                )
            } ?: throw ServerException("获取当前登录用户信息失败！")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 签收任务
     *
     * @param taskId 任务id
     */
    @Transactional
    @Throws(ServerException::class)
    fun claimTask(taskId: String) {
        try {
            commonOauthServer.userInfo()?.let { userInfo ->
                val task = taskService.createTaskQuery().taskId(taskId).singleResult()
                if (task == null) {
                    logAdapter.error("流程任务【$taskId】不存在！")
                    throw ServerException("流程任务不存在！")
                }
                taskCreateListener.findTaskPendingUserIdList(taskId).forEach { userId ->
                    taskCreateListener.notifyPendingFinished(taskId, userId)
                }
                taskService.claim(taskId, userInfo.id)
                taskCreateListener.notifyPendingCreated(taskId, userInfo.id!!)
            } ?: throw ServerException("获取当前登录用户信息失败！")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }
    }

    /**
     * 转办任务
     *
     * @param taskId 任务id
     */
    @Transactional
    @Throws(ServerException::class)
    fun turnTask(taskId: String, acceptUserId: String) {
        try {
            commonOauthServer.userInfo()?.let { userInfo ->
                val task = taskService.createTaskQuery().taskId(taskId).singleResult()
                if (task == null) {
                    logAdapter.error("流程任务【$taskId】不存在！")
                    throw ServerException("流程任务不存在！")
                }
                taskService.setOwner(taskId, userInfo.id)
                taskCreateListener.notifyPendingFinished(taskId, userInfo.id!!)
                taskService.setAssignee(taskId, acceptUserId)
                taskCreateListener.notifyPendingCreated(taskId, acceptUserId)
            } ?: throw ServerException("获取当前登录用户信息失败！")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }
    }

    /**
     * 委派任务
     *
     * @param taskId 任务id
     */
    @Transactional
    @Throws(ServerException::class)
    fun delegateTask(taskId: String, acceptUserId: String) {
        try {
            commonOauthServer.userInfo()?.let { userInfo ->
                val task = taskService.createTaskQuery().taskId(taskId).singleResult()
                if (task == null) {
                    logAdapter.error("流程任务【$taskId】不存在！")
                    throw ServerException("流程任务不存在！")
                }
                taskService.setOwner(taskId, userInfo.id)
                taskCreateListener.notifyPendingFinished(taskId, userInfo.id!!)
                taskService.delegateTask(taskId, acceptUserId)
                taskCreateListener.notifyPendingCreated(taskId, acceptUserId)
            } ?: throw ServerException("获取当前登录用户信息失败！")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }
    }

    /**
     * 任务处理
     *
     * @param processHandlingPo 任务处理参数
     */
    @Transactional
    @Throws(ServerException::class)
    fun processTask(processHandlingPo: ProcessHandlingPo, userId: String) {
        try {
            val task = taskService.createTaskQuery().taskId(processHandlingPo.taskId).singleResult()
            if (task == null) {
                logAdapter.error("流程任务【${processHandlingPo.taskId}】不存在！")
                throw ServerException("流程任务不存在！")
            }
            val params: MutableMap<String, Any> = mutableMapOf()
            processHandlingPo.params.forEach { (key, value) ->
                if (!params.containsKey(key)) {
                    params[key] = value
                }
            }
            val comment = if (processHandlingPo.pass!!) {
                if (CommonTools.isNullStr(processHandlingPo.comment)) {
                    "通过"
                } else {
                    processHandlingPo.comment!!
                }
            } else {
                if (CommonTools.isNullStr(processHandlingPo.comment)) {
                    "不通过"
                } else {
                    processHandlingPo.comment!!
                }
            }
            params[WorkFlowParamKey.comment] = if (task.delegationState == DelegationState.PENDING) {
                (getUserById(userId).name ?: "") + ":" + comment
            } else {
                comment
            }
            params[WorkFlowParamKey.pass] = processHandlingPo.pass!!
            runtimeService.setVariablesLocal(task.executionId, processHandlingPo.taskParams)
            if (task.delegationState == DelegationState.PENDING) {
                taskService.resolveTask(task.id, params)
                taskCreateListener.notifyPendingFinished(task.id, userId)
                taskCreateListener.notifyPendingCreated(task.id, task.owner)
            } else {
                taskService.complete(task.id, params)
                taskCreateListener.notifyPendingFinished(task.id, userId)
            }
            // 添加至我处理过的流程实例
            myProcessInstanceRepository.findByUserIdAndProcessInstanceId(userId, task.processInstanceId).let {
                if (!it.isPresent) {
                    val processInstance = findProcessInstance(task.processInstanceId)
                    myProcessInstanceRepository.save(
                        MyProcessInstance(
                            processInstanceId = processInstance.processInstanceId!!,
                            processDefinitionKey = processInstance.processDefinitionKey!!,
                            businessKey = processInstance.businessKey!!,
                            startUserId = processInstance.startUser?.id,
                            userId = userId,
                            startTime = processInstance.startTime
                        )
                    )
                }
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun deleteProcessInstance(processTerminationPo: ProcessTerminationPo) {
        taskService.createTaskQuery().processInstanceId(processTerminationPo.processInstanceId).list().forEach { task ->
            taskCreateListener.findTaskPendingUserIdList(task.id).forEach { userId ->
                taskCreateListener.notifyPendingFinished(task.id, userId)
            }
        }
        runtimeService.deleteProcessInstance(processTerminationPo.processInstanceId, processTerminationPo.reason)
    }

    @Throws(ServerException::class)
    fun findProcessInstance(processInstanceId: String): ProcessInstanceVo =
        try {
            if (isFinished(processInstanceId)) {
                historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult()?.let { instance ->
                        instanceToVo(instance)
                    }
            } else {
                runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult()?.let { instance ->
                        instanceToVo(instance)
                    }
            } ?: throw ServerException("流程实例不存在！")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 查询当前用户处理过的流程实例
     */
    @Throws(ServerException::class)
    fun findProcessInstanceForMyProcess(processQueryPo: ProcessQueryPo): CustomerQueryPageVo<ProcessInstanceVo> =
        try {
            commonOauthServer.userInfo()?.let { userInfo ->
                myProcessInstanceRepository.findAll({ root, _, criteriaBuilder ->
                    val predicateList: MutableList<Predicate> = mutableListOf()
                    predicateList.add(
                        criteriaBuilder.equal(
                            root.get<Any>("userId").`as`(String::class.java),
                            userInfo.id
                        )
                    )
                    if (processQueryPo.processDefinitionKeys != null && processQueryPo.processDefinitionKeys!!.isNotEmpty()) {
                        predicateList.add(
                            root.get<Any>("processDefinitionKey").`as`(String::class.java)
                                .`in`(processQueryPo.processDefinitionKeys)
                        )
                    }
                    if (!CommonTools.isNullStr(processQueryPo.processBusinessKey)) {
                        predicateList.add(
                            criteriaBuilder.equal(
                                root.get<Any>("businessKey").`as`(String::class.java),
                                processQueryPo.processBusinessKey
                            )
                        )
                    }
                    if (!CommonTools.isNullStr(processQueryPo.startUserId)) {
                        predicateList.add(
                            criteriaBuilder.equal(
                                root.get<Any>("startUserId").`as`(String::class.java),
                                processQueryPo.startUserId
                            )
                        )
                    }
                    if (processQueryPo.startTime != null) {
                        predicateList.add(
                            criteriaBuilder.ge(
                                root.get<Any>("startTime").`as`(Long::class.java),
                                processQueryPo.startTime
                            )
                        )
                    }
                    if (processQueryPo.endTime != null) {
                        predicateList.add(
                            criteriaBuilder.le(
                                root.get<Any>("startTime").`as`(Long::class.java),
                                processQueryPo.endTime
                            )
                        )
                    }
                    criteriaBuilder.and(*predicateList.toTypedArray())
                }, buildPageRequest(processQueryPo.queryParam!!)).let {
                    CustomerQueryPageVo(
                        currPage = processQueryPo.queryParam!!.currPage!!.toLong(),
                        pageSize = processQueryPo.queryParam!!.pageSize!!.toLong(),
                        totalElements = it.totalElements,
                        content = it.content.map { instance ->
                            findProcessInstance(instance.processInstanceId)
                        }
                    )
                }
            } ?: throw ServerException("获取当前登录用户信息失败！")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 查询流程实例
     */
    @Throws(ServerException::class)
    fun findProcessInstance(processQueryPo: ProcessQueryPo): CustomerQueryPageVo<ProcessInstanceVo> =
        try {
            val firstResult = (processQueryPo.queryParam!!.currPage!! - 1) * processQueryPo.queryParam!!.pageSize!!
            val maxResult = processQueryPo.queryParam!!.pageSize!!
            val processInstanceQuery = runtimeService.createProcessInstanceQuery()
            if (processQueryPo.processDefinitionKeys != null && processQueryPo.processDefinitionKeys!!.isNotEmpty()) {
                processInstanceQuery.processDefinitionKeys(processQueryPo.processDefinitionKeys!!.toSet())
            }
            if (processQueryPo.processInstanceIds != null && processQueryPo.processInstanceIds!!.isNotEmpty()) {
                processInstanceQuery.processInstanceIds(processQueryPo.processInstanceIds!!.toSet())
            }
            if (!CommonTools.isNullStr(processQueryPo.processBusinessKey)) {
                processInstanceQuery.processInstanceBusinessKeyLike(processQueryPo.processBusinessKey)
            }
            if (!CommonTools.isNullStr(processQueryPo.startUserId)) {
                processInstanceQuery.startedBy(processQueryPo.startUserId)
            }
            if (processQueryPo.startTime != null) {
                processInstanceQuery.startedAfter(DateTime(processQueryPo.startTime!!).toDate())
            }
            if (processQueryPo.endTime != null) {
                processInstanceQuery.startedBefore(DateTime(processQueryPo.endTime!!).toDate())
            }
            val total = processInstanceQuery.list().count()
            val list = processInstanceQuery.orderByStartTime().asc()
                .listPage(firstResult, maxResult)
                .map { instance -> instanceToVo(instance) }
            CustomerQueryPageVo(
                currPage = processQueryPo.queryParam!!.currPage!!.toLong(),
                pageSize = processQueryPo.queryParam!!.pageSize!!.toLong(),
                totalElements = total.toLong(),
                content = list
            )
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 查询流程实例
     */
    @Throws(ServerException::class)
    fun findHistoryProcessInstance(processQueryPo: ProcessQueryPo): CustomerQueryPageVo<ProcessInstanceVo> =
        try {
            val firstResult = (processQueryPo.queryParam!!.currPage!! - 1) * processQueryPo.queryParam!!.pageSize!!
            val maxResult = processQueryPo.queryParam!!.pageSize!!
            val processInstanceQuery = historyService.createHistoricProcessInstanceQuery()
            processInstanceQuery.finished()
            if (processQueryPo.processDefinitionKeys != null && processQueryPo.processDefinitionKeys!!.isNotEmpty()) {
                processInstanceQuery.processDefinitionKeyIn(processQueryPo.processDefinitionKeys!!)
            }
            if (processQueryPo.processInstanceIds != null && processQueryPo.processInstanceIds!!.isNotEmpty()) {
                processInstanceQuery.processInstanceIds(processQueryPo.processInstanceIds!!.toSet())
            }
            if (!CommonTools.isNullStr(processQueryPo.processBusinessKey)) {
                processInstanceQuery.processInstanceBusinessKeyLike(processQueryPo.processBusinessKey)
            }
            if (!CommonTools.isNullStr(processQueryPo.startUserId)) {
                processInstanceQuery.startedBy(processQueryPo.startUserId)
            }
            if (processQueryPo.startTime != null) {
                processInstanceQuery.startedAfter(DateTime(processQueryPo.startTime!!).toDate())
            }
            if (processQueryPo.endTime != null) {
                processInstanceQuery.startedBefore(DateTime(processQueryPo.endTime!!).toDate())
            }
            val total = processInstanceQuery.list().count()
            val list = processInstanceQuery.orderByProcessInstanceStartTime().asc()
                .listPage(firstResult, maxResult)
                .map { instance -> instanceToVo(instance) }
            CustomerQueryPageVo(
                currPage = processQueryPo.queryParam!!.currPage!!.toLong(),
                pageSize = processQueryPo.queryParam!!.pageSize!!.toLong(),
                totalElements = total.toLong(),
                content = list
            )
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 查询指定流程的历史信息
     *
     * @param processInstanceId 流程id
     * @return 流程历史信息
     */
    @Throws(ServerException::class)
    fun findHistoryActivity(processInstanceId: String): List<ProcessHistoryActivityVo> =
        try {
            val historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult()
            if (historicProcessInstance == null) {
                logAdapter.error("流程实例【$processInstanceId】不存在")
                throw ServerException("流程实例不存在！")
            }
            historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc().list()
                .filter { historicActivityInstance -> !CommonTools.isNullStr(historicActivityInstance.taskId) }
                .map { historicActivityInstance ->
                    actToVo(
                        historicActivityInstance,
                        historicProcessInstance.businessKey
                    )
                }
                .filter { processHistoryActivityVo -> !CommonTools.isNullStr(processHistoryActivityVo.comment) }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 获取下一步流程节点列表
     *
     * @param taskId 当前任务id
     * @return 流程节点列表
     * @throws ServerException 异常
     */
    @Throws(ServerException::class)
    fun getNextFlowElementList(taskId: String): List<FlowElement> =
        try {
            val task = taskService.createTaskQuery().taskId(taskId).singleResult()
            if (task == null) {
                logAdapter.error("流程任务【$taskId】不存在！")
                throw ServerException("流程任务不存在！")
            }
            val execution = runtimeService.createExecutionQuery().executionId(task.executionId).singleResult()
            if (execution == null) {
                logAdapter.error("流程任务【$taskId】不存在处理信息！")
                throw ServerException("流程任务不存在！")
            }
            (repositoryService.getBpmnModel(task.processDefinitionId).getFlowElement(execution.activityId) as FlowNode)
                .outgoingFlows.map { it.targetFlowElement }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 生成流程图
     *
     * @param processInstanceId 流程实例id
     * @param imgType 图片格式
     * @return 流程图输入流
     * @throws ServerException 异常
     */
    @Throws(ServerException::class)
    fun generateDiagram(processInstanceId: String, imgType: String): InputStream =
        try {
            val processDefinitionId = if (isFinished(processInstanceId)) {
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
                    .singleResult()?.processDefinitionId
                    ?: throw ServerException("获取流程实例失败")
            } else {
                runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                    .singleResult()?.processDefinitionId
                    ?: throw ServerException("获取流程实例失败")
            }
            // 将当前节点ID放入高亮显示节点集合
            val activityIdList: MutableList<String> = mutableListOf()
            taskService.createTaskQuery().processInstanceId(processInstanceId).active().list().forEach { task ->
                activityIdList.addAll(runtimeService.getActiveActivityIds(task.executionId))
            }
            // 将流经的连线放入高亮显示连线集合
            val flowIdList: List<String> = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW).list().map { flow -> flow.activityId }

            //获取流程图
            val model = repositoryService.getBpmnModel(processDefinitionId)
            val engineConfiguration = processEngine.processEngineConfiguration
            val diagramGenerator = engineConfiguration.processDiagramGenerator
            diagramGenerator.generateDiagram(
                model,
                imgType.lowercase(),
                activityIdList,
                flowIdList,
                engineConfiguration.activityFontName,
                engineConfiguration.labelFontName,
                engineConfiguration.annotationFontName,
                engineConfiguration.classLoader,
                1.0,
                true
            )
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }

    /**
     * 流程是否结束
     *
     * @param processInstanceId 流程实例id
     * @return true|false
     */
    fun isFinished(processInstanceId: String): Boolean {
        return historyService.createHistoricProcessInstanceQuery().finished().processInstanceId(processInstanceId)
            .count() > 0
    }
}
