package pers.acp.admin.workflow.domain

import org.flowable.bpmn.model.BpmnModel
import org.flowable.bpmn.model.FlowElement
import org.flowable.bpmn.model.FlowNode
import org.flowable.bpmn.model.SequenceFlow
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
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.common.feign.CommonOauthServer
import pers.acp.admin.common.po.*
import pers.acp.admin.common.vo.CustomerQueryPageVo
import pers.acp.admin.common.vo.ProcessHistoryActivityVo
import pers.acp.admin.common.vo.ProcessInstanceVo
import pers.acp.admin.common.vo.ProcessTaskVo
import pers.acp.admin.workflow.constant.WorkFlowParamKey
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import java.io.InputStream

/**
 * @author zhang by 10/06/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class WorkFlowDomain @Autowired
constructor(private val logAdapter: LogAdapter,
            private val commonOauthServer: CommonOauthServer,
            private val formService: FormService,
            private val runtimeService: RuntimeService,
            private val taskService: TaskService,
            private val repositoryService: RepositoryService,
            private val historyService: HistoryService,
            @param:Qualifier("processEngine") private val processEngine: ProcessEngine) : BaseDomain() {

    /**
     * 任务实体转换
     *
     * @param task 任务对象
     * @return 转换后任务对象
     */
    private fun taskToVo(task: Task) =
            runtimeService.createProcessInstanceQuery().processInstanceId(task.processInstanceId).singleResult().let { processInstance ->
                val params = runtimeService.getVariables(task.executionId)
                ProcessTaskVo(
                        processInstanceId = task.processInstanceId,
                        name = task.name,
                        taskId = task.id,
                        parentTaskId = task.parentTaskId,
                        executionId = task.executionId,
                        params = params,
                        businessKey = params[WorkFlowParamKey.businessKey]?.toString() ?: "",
                        unClaimed = task.assignee == null,
                        userId = task.assignee,
                        localParams = task.taskLocalVariables,
                        properties = formService.getTaskFormData(task.id).formProperties.associateBy({ it.name }, { it.value }).toMutableMap(),
                        createTime = task.createTime.time,
                        claimTime = task.claimTime?.time,
                        processDefinitionKey = processInstance.processDefinitionKey,
                        flowName = processInstance.processDefinitionName,
                        title = params[WorkFlowParamKey.title]?.toString() ?: "",
                        description = params[WorkFlowParamKey.description]?.toString() ?: "",
                        startUserId = processInstance.startUserId,
                        taskOwnerUserId = task.owner,
                        delegated = task.delegationState == DelegationState.PENDING
                )
            }

    /**
     * 历史记录实例转换
     *
     * @param historicActivityInstance 历史记录
     * @return 转换后对象
     */
    private fun actToVo(historicActivityInstance: HistoricActivityInstance, businessKey: String): ProcessHistoryActivityVo =
            historyService.createHistoricDetailQuery().activityInstanceId(historicActivityInstance.id).let { historicDetailQuery ->
                val params: MutableMap<String, Any> = mutableMapOf()
                val localParams: MutableMap<String, Any> = mutableMapOf()
                historicDetailQuery.list()
                        .filter { historicDetail -> CommonTools.isNullStr(historicDetail.taskId) }
                        .forEach { historicDetail ->
                            params[(historicDetail as HistoricVariableUpdate).variableName] = historicDetail.value
                        }
                historicDetailQuery.taskId(historicActivityInstance.taskId).list()
                        .forEach { historicDetail ->
                            params[(historicDetail as HistoricVariableUpdate).variableName] = historicDetail.value

                        }
                ProcessHistoryActivityVo(
                        processInstanceId = historicActivityInstance.processInstanceId,
                        activityId = historicActivityInstance.activityId,
                        activityName = historicActivityInstance.activityName,
                        taskId = historicActivityInstance.taskId,
                        executionId = historicActivityInstance.executionId,
                        businessKey = businessKey,
                        userId = historicActivityInstance.assignee,
                        pass = params[WorkFlowParamKey.pass] as Boolean,
                        comment = params[WorkFlowParamKey.comment].toString(),
                        params = params,
                        localParams = localParams,
                        startTime = historicActivityInstance.startTime.time,
                        endTime = historicActivityInstance.endTime.time
                )
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
                    val params = processInstance.processVariables
                    ProcessInstanceVo(
                            isFinished = false,
                            processDefinitionKey = processInstance.processDefinitionKey,
                            businessKey = processInstance.businessKey,
                            flowName = params[WorkFlowParamKey.flowName]?.toString() ?: "",
                            title = params[WorkFlowParamKey.title]?.toString() ?: "",
                            description = params[WorkFlowParamKey.description]?.toString() ?: "",
                            startUserId = processInstance.startUserId,
                            params = params,
                            startTime = processInstance.startTime!!.time
                    )
                }
                is HistoricProcessInstance -> {
                    val params = processInstance.processVariables
                    ProcessInstanceVo(
                            isFinished = true,
                            processDefinitionKey = processInstance.processDefinitionKey,
                            businessKey = processInstance.businessKey,
                            flowName = params[WorkFlowParamKey.flowName]?.toString() ?: "",
                            title = params[WorkFlowParamKey.title]?.toString() ?: "",
                            description = params[WorkFlowParamKey.description]?.toString() ?: "",
                            startUserId = processInstance.startUserId,
                            params = params,
                            startTime = processInstance.startTime!!.time,
                            endTime = processInstance.endTime!!.time
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
     * @return 流程实例id
     */
    @Transactional
    @Throws(ServerException::class)
    fun startFlow(processStartPo: ProcessStartPo): String =
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
                Authentication.setAuthenticatedUserId(processStartPo.startUserId)
                runtimeService.startProcessInstanceByKey(processStartPo.processDefinitionKey, processStartPo.businessKey, params)
                        .id
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
                throw ServerException(e.message)
            }

    /**
     * 获取任务信息
     *
     * @param taskId 任务ID
     * @return 任务列表
     */
    @Throws(ServerException::class)
    fun findTaskId(taskId: String): ProcessTaskVo =
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
     *
     * @return 任务列表
     */
    @Throws(ServerException::class)
    fun findTaskList(processQueryPo: ProcessQueryPo): CustomerQueryPageVo<ProcessTaskVo> =
            try {
                val firstResult = processQueryPo.queryParam!!.currPage - 1 * processQueryPo.queryParam!!.pageSize
                val maxResult = firstResult + processQueryPo.queryParam!!.pageSize
                commonOauthServer.userInfo()?.let { userInfo ->
                    val taskQuery = taskService.createTaskQuery().or()
                            .taskCandidateGroupIn(userInfo.roleSet.map { role -> role.code }.toList())
                            .taskCandidateOrAssigned(userInfo.id).endOr()
                    if (processQueryPo.processInstanceIds != null && processQueryPo.processInstanceIds!!.isNotEmpty()) {
                        taskQuery.processInstanceIdIn(processQueryPo.processInstanceIds!!)
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
                            currPage = processQueryPo.queryParam!!.currPage.toLong(),
                            pageSize = processQueryPo.queryParam!!.pageSize.toLong(),
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
                taskService.claim(taskId, userInfo.id)
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
                taskService.setAssignee(taskId, acceptUserId)
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
                taskService.delegateTask(taskId, acceptUserId)
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
    fun processTask(processHandlingPo: ProcessHandlingPo) {
        try {
            commonOauthServer.userInfo()?.let { userInfo ->
                val task = taskService.createTaskQuery().taskId(processHandlingPo.taskId).singleResult()
                if (task == null) {
                    logAdapter.error("流程任务【${processHandlingPo.taskId}】不存在！")
                    throw ServerException("流程任务不存在！")
                }
                val params: MutableMap<String, Any> = mutableMapOf()
                var comment = processHandlingPo.comment
                if (CommonTools.isNullStr(comment)) {
                    comment = if (processHandlingPo.pass!!) {
                        "通过"
                    } else {
                        "不通过"
                    }
                }
                params[WorkFlowParamKey.pass] = processHandlingPo.pass!!
                params[WorkFlowParamKey.comment] = comment!!
                processHandlingPo.params.forEach { (key, value) ->
                    if (!params.containsKey(key)) {
                        params[key] = value
                    }
                }
                runtimeService.setVariablesLocal(task.executionId, processHandlingPo.taskParams)
                if (task.delegationState == DelegationState.PENDING) {
                    taskService.resolveTask(task.id, params)
                } else {
                    taskService.complete(task.id, params)
                }
            } ?: throw ServerException("获取当前登录用户信息失败！")
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
            throw ServerException(e.message)
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun deleteProcessInstance(processTerminationPo: ProcessTerminationPo) {
        runtimeService.deleteProcessInstance(processTerminationPo.processInstanceId, processTerminationPo.reason)
    }

    @Throws(ServerException::class)
    fun findProcessInstance(processInstanceId: String): ProcessInstanceVo =
            try {
                if (isFinished(processInstanceId)) {
                    historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(processInstanceId).singleResult()
                            .let { instance ->
                                instanceToVo(instance)
                            }
                } else {
                    runtimeService.createProcessInstanceQuery()
                            .processInstanceId(processInstanceId).singleResult()
                            .let { instance ->
                                instanceToVo(instance)
                            }
                }
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
                val firstResult = processQueryPo.queryParam!!.currPage - 1 * processQueryPo.queryParam!!.pageSize
                val maxResult = firstResult + processQueryPo.queryParam!!.pageSize
                val processInstanceQuery = runtimeService.createProcessInstanceQuery()
                if (processQueryPo.processInstanceIds != null && processQueryPo.processInstanceIds!!.isNotEmpty()) {
                    processInstanceQuery.processInstanceIds(processQueryPo.processInstanceIds!!.toSet())
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
                CustomerQueryPageVo<ProcessInstanceVo>(
                        currPage = processQueryPo.queryParam!!.currPage.toLong(),
                        pageSize = processQueryPo.queryParam!!.pageSize.toLong(),
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
                val firstResult = processQueryPo.queryParam!!.currPage - 1 * processQueryPo.queryParam!!.pageSize
                val maxResult = firstResult + processQueryPo.queryParam!!.pageSize
                val processInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                if (processQueryPo.processInstanceIds != null && processQueryPo.processInstanceIds!!.isNotEmpty()) {
                    processInstanceQuery.processInstanceIds(processQueryPo.processInstanceIds!!.toSet())
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
                CustomerQueryPageVo<ProcessInstanceVo>(
                        currPage = processQueryPo.queryParam!!.currPage.toLong(),
                        pageSize = processQueryPo.queryParam!!.pageSize.toLong(),
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
     * @param processInstanceId 流程id，为空或null表示查询所有
     * @return 流程历史信息
     */
    @Throws(ServerException::class)
    fun findHistoryActivity(processInstanceId: String): List<ProcessHistoryActivityVo> =
            try {
                val historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult()
                if (historicProcessInstance == null) {
                    logAdapter.error("流程实例【$processInstanceId】不存在")
                    throw ServerException("流程实例不存在！")
                }
                historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).finished()
                        .orderByHistoricActivityInstanceEndTime().asc().list()
                        .filter { historicActivityInstance -> !CommonTools.isNullStr(historicActivityInstance.taskId) }
                        .map { historicActivityInstance -> actToVo(historicActivityInstance, historicProcessInstance.businessKey) }
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
                    historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().processDefinitionId
                } else {
                    runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().processDefinitionId
                }
                // 将已经执行的节点ID放入高亮显示节点集合
                val historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                        .orderByHistoricActivityInstanceStartTime().asc().list()
                val highLightedActivityIdList: MutableList<String> = mutableListOf()
                historicActivityInstanceList.forEach { highLightedActivityIdList.add(it.activityId) }

                //获取流程图
                val model = repositoryService.getBpmnModel(processDefinitionId)
                // 获取已流经的流程线，需要高亮显示高亮流程已发生流转的线id集合
                val flows: MutableList<String> = getHighLightedFlows(model, historicActivityInstanceList)
                val engineConfiguration = processEngine.processEngineConfiguration
                val diagramGenerator = engineConfiguration.processDiagramGenerator
                diagramGenerator.generateDiagram(model, imgType.toLowerCase(), highLightedActivityIdList, flows,
                        engineConfiguration.activityFontName, engineConfiguration.labelFontName, engineConfiguration.annotationFontName,
                        engineConfiguration.classLoader, 1.0, true)
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
        return historyService.createHistoricProcessInstanceQuery().finished().processInstanceId(processInstanceId).count() > 0
    }

    /**
     * 获取已流经的流程线，需要高亮显示高亮流程已发生流转的线id集合
     * @param model
     * @param historicActivityInstanceList
     */
    private fun getHighLightedFlows(model: BpmnModel, historicActivityInstanceList: List<HistoricActivityInstance>): MutableList<String> {
        // 已流经的流程线，需要高亮显示
        val highLightedFlowIdList: MutableList<String> = mutableListOf()
        // 全部活动节点
        val allHistoricActivityNodeList: MutableList<FlowNode> = mutableListOf()
        // 已完成的历史活动节点
        val finishedActivityInstanceList: MutableList<HistoricActivityInstance> = mutableListOf()
        historicActivityInstanceList.forEach {
            // 获取流程节点
            val element = model.mainProcess.getFlowElement(it.activityId, true)
            if (element is FlowNode) {
                allHistoricActivityNodeList.add(element)
                // 结束时间不为空，当前节点则已经完成
                it.endTime?.apply { finishedActivityInstanceList.add(it) }
            }
        }
        // 当前流程节点
        var currentFlowNode: FlowNode?
        // 目标流程节点
        var targetFlowNode: FlowNode?
        // 当前活动实例
        var currentActivityInstance: HistoricActivityInstance
        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (k in finishedActivityInstanceList.indices) {
            currentActivityInstance = finishedActivityInstanceList[k]
            currentFlowNode = model.mainProcess.getFlowElement(currentActivityInstance
                    .activityId, true) as FlowNode
            // 当前节点的所有流出线
            val outgoingFlowList: List<SequenceFlow> = currentFlowNode.outgoingFlows
            /**
             * 遍历outgoingFlows并找到已流转的 满足如下条件认为已流转：
             * 1.当前节点是并行网关或兼容网关，则通过outgoingFlows能够在历史活动中找到的全部节点均为已流转
             * 2.当前节点是以上两种类型之外的，通过outgoingFlows查找到的时间最早的流转节点视为有效流转
             * (第2点有问题，有过驳回的，会只绘制驳回的流程线，通过走向下一级的流程线没有高亮显示)
             */
            if ("parallelGateway" == currentActivityInstance.activityType || "inclusiveGateway" == currentActivityInstance.activityType) {
                // 遍历历史活动节点，找到匹配流程目标节点的
                outgoingFlowList.forEach {
                    // 获取当前节点流程线对应的下级节点
                    targetFlowNode = model.mainProcess.getFlowElement(it.targetRef,
                            true) as FlowNode
                    // 如果下级节点包含在所有历史节点中，则将当前节点的流出线高亮显示
                    targetFlowNode?.apply {
                        if (allHistoricActivityNodeList.contains(this)) {
                            highLightedFlowIdList.add(it.id)
                        }
                    }
                }
            } else {
                /**
                 * 2、当前节点不是并行网关或兼容网关
                 * 【已解决-问题】如果当前节点有驳回功能，驳回到申请节点，
                 * 则因为申请节点在历史节点中，导致当前节点驳回到申请节点的流程线被高亮显示，但实际并没有进行驳回操作
                 */
                // 当前节点ID
                val currentActivityId: String = currentActivityInstance.activityId
                var ifStartFind = false
                var ifFinded = false
                var historicActivityInstance: HistoricActivityInstance
                // 循环当前节点的所有流出线
                // 循环所有历史节点
                for (i in historicActivityInstanceList.indices) {
                    // 如果当前节点流程线对应的下级节点在历史节点中，则该条流程线进行高亮显示（【问题】有驳回流程线时，即使没有进行驳回操作，因为申请节点在历史节点中，也会将驳回流程线高亮显示-_-||）
                    // 历史节点
                    historicActivityInstance = historicActivityInstanceList[i]
                    // 如果循环历史节点中的id等于当前节点id，从当前历史节点继续先后查找是否有当前节点流程线等于的节点
                    // 历史节点的序号需要大于等于已完成历史节点的序号，防止驳回重审一个节点经过两次是只取第一次的流出线高亮显示，第二次的不显示
                    if (i >= k && historicActivityInstance.activityId == currentActivityId) {
                        ifStartFind = true
                        // 跳过当前节点继续查找下一个节点
                        continue
                    }
                    if (ifStartFind) {
                        ifFinded = false
                        for (sequenceFlow in outgoingFlowList) {
                            // 如果当前节点流程线对应的下级节点在其后面的历史节点中，则该条流程线进行高亮显示
                            if (historicActivityInstance.activityId == sequenceFlow.targetRef) {
                                highLightedFlowIdList.add(sequenceFlow.id)
                                // 暂时默认找到离当前节点最近的下一级节点即退出循环，否则有多条流出线时将全部被高亮显示
                                ifFinded = true
                                break
                            }
                        }
                    }
                    if (ifFinded) {
                        // 暂时默认找到离当前节点最近的下一级节点即退出历史节点循环，否则有多条流出线时将全部被高亮显示
                        break
                    }
                }
            }
        }
        return highLightedFlowIdList
    }

}
