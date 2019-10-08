package pers.acp.admin.workflow.domain

import org.flowable.bpmn.model.BpmnModel
import org.flowable.bpmn.model.FlowElement
import org.flowable.bpmn.model.FlowNode
import org.flowable.bpmn.model.SequenceFlow
import org.flowable.engine.*
import org.flowable.engine.history.HistoricActivityInstance
import org.flowable.engine.history.HistoricVariableUpdate
import org.flowable.task.api.TaskInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.common.vo.FlowHistoryVo
import pers.acp.admin.common.vo.FlowTaskVo
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
    private fun taskToVO(task: TaskInfo) = FlowTaskVo(
            processInstanceId = task.processInstanceId,
            name = task.name,
            taskId = task.id,
            parentTaskId = task.parentTaskId,
            executionId = task.executionId,
            businessKey = runtimeService.getVariable(task.executionId, WorkFlowParamKey.businessKey, String::class.java),
            params = runtimeService.getVariables(task.executionId),
            userId = task.assignee,
            localParams = task.taskLocalVariables,
            createTime = task.createTime.time
    )

    /**
     * 历史记录实例转换
     *
     * @param historicActivityInstance 历史记录
     * @return 转换后对象
     */
    private fun actToVO(historicActivityInstance: HistoricActivityInstance, businessKey: String): FlowHistoryVo {
        val historicDetailQuery = historyService.createHistoricDetailQuery().activityInstanceId(historicActivityInstance.id)
        val params: MutableMap<String, Any> = mutableMapOf()
        val localParams: MutableMap<String, Any> = mutableMapOf()
        historicDetailQuery.list().filter { historicDetail -> CommonTools.isNullStr(historicDetail.taskId) }.forEach { historicDetail ->
            params[(historicDetail as HistoricVariableUpdate).variableName] = historicDetail.value
        }
        historicDetailQuery.taskId(historicActivityInstance.taskId).list().forEach { historicDetail ->
            params[(historicDetail as HistoricVariableUpdate).variableName] = historicDetail.value

        }
        return FlowHistoryVo(
                processInstanceId = historicActivityInstance.processInstanceId,
                activityId = historicActivityInstance.activityId,
                activityName = historicActivityInstance.activityName,
                taskId = historicActivityInstance.taskId,
                executionId = historicActivityInstance.executionId,
                businessKey = businessKey,
                userId = historicActivityInstance.assignee,
                isApproved = params[WorkFlowParamKey.approved] as Boolean,
                comment = params[WorkFlowParamKey.comment].toString(),
                params = params,
                localParams = localParams,
                createTime = historicActivityInstance.startTime.time,
                endTime = historicActivityInstance.endTime.time
        )
    }

    /**
     * 启动流程
     *
     * @param processDefinitionKey 流程键
     * @param businessKey          业务键
     * @return 流程实例id
     */
    @Transactional
    @Throws(ServerException::class)
    fun startFlow(processDefinitionKey: String, businessKey: String, params: MutableMap<String, Any>): String =
            try {
                params[WorkFlowParamKey.businessKey] = businessKey
                runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, params)
                        .id
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
                throw ServerException(e.message)
            }

    /**
     * 获取用户待办任务
     *
     * @param userId 用户id
     * @return 任务列表
     */
    @Throws(ServerException::class)
    fun findTaskListByUserId(userId: String): List<FlowTaskVo> =
            try {
                taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list()
                        .map { task -> taskToVO(task) }
            } catch (e: Exception) {
                logAdapter.error(e.message, e)
                throw ServerException(e.message)
            }

    /**
     * 审批通过
     *
     * @param taskId  当前任务id
     * @param comment 审批意见
     * @param params  附加参数变量
     * @throws ServerException 异常
     */
    @Transactional
    @Throws(ServerException::class)
    fun pass(taskId: String, comment: String?, params: Map<String, Any>, taskParams: Map<String, Any>) {
        var text = comment
        if (CommonTools.isNullStr(text)) {
            text = "审批通过"
        }
        approved(taskId, true, text!!, params, taskParams)
    }

    /**
     * 审批不通过
     *
     * @param taskId  当前任务id
     * @param comment 审批意见
     * @param params  附加参数变量
     * @throws ServerException 异常
     */
    @Transactional
    @Throws(ServerException::class)
    fun noPass(taskId: String, comment: String?, params: Map<String, Any>, taskParams: Map<String, Any>) {
        var text = comment
        if (CommonTools.isNullStr(text)) {
            text = "审批不通过"
        }
        approved(taskId, false, text!!, params, taskParams)
    }

    /**
     * 审批处理
     *
     * @param taskId   当前任务id
     * @param approved 审批结果（true-通过，false-不通过）
     * @param comment  审批意见
     * @param params   附加参数变量
     * @throws ServerException 异常
     */
    @Throws(ServerException::class)
    private fun approved(taskId: String, approved: Boolean, comment: String, params: Map<String, Any>, taskParams: Map<String, Any>) =
            try {
                val task = taskService.createTaskQuery().taskId(taskId).singleResult()
                if (task == null) {
                    logAdapter.error("流程任务【$taskId】不存在！")
                    throw ServerException("流程任务不存在！")
                }
                //通过审核
                val map: MutableMap<String, Any> = mutableMapOf()
                map[WorkFlowParamKey.approved] = approved
                map[WorkFlowParamKey.comment] = comment
                params.forEach { (key, value) ->
                    if (!map.containsKey(key)) {
                        map[key] = value
                    }
                }
                runtimeService.setVariablesLocal(task.executionId, taskParams)
                taskService.complete(taskId, map)
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
    fun findHistoryInfo(processInstanceId: String): List<FlowHistoryVo> =
            try {
                val historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult()
                if (historicProcessInstance == null) {
                    logAdapter.error("流程实例【$processInstanceId】不存在")
                    throw ServerException("流程实例不存在！")
                }
                historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).finished()
                        .orderByHistoricActivityInstanceEndTime().asc().list()
                        .filter { historicActivityInstance -> !CommonTools.isNullStr(historicActivityInstance.taskId) }
                        .map { historicActivityInstance -> actToVO(historicActivityInstance, historicProcessInstance.businessKey) }
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
     * @return 流程图输入流
     * @throws ServerException 异常
     */
    @Throws(ServerException::class)
    fun generateDiagram(processInstanceId: String): InputStream =
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
                diagramGenerator.generateDiagram(model, "bmp", highLightedActivityIdList, flows,
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
            val flowNode = model.mainProcess.getFlowElement(it.activityId, true) as FlowNode
            allHistoricActivityNodeList.add(flowNode)
            // 结束时间不为空，当前节点则已经完成
            it.endTime?.apply { finishedActivityInstanceList.add(it) }
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
