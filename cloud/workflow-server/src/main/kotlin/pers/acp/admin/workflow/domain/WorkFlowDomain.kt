package pers.acp.admin.workflow.domain

import org.flowable.bpmn.model.FlowElement
import org.flowable.bpmn.model.FlowNode
import org.flowable.engine.*
import org.flowable.engine.history.*
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
                    val pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult()
                    pi.processDefinitionId
                } else {
                    val pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult()
                    pi.processDefinitionId
                }
                val activityIdList: MutableList<String> = mutableListOf()
                val historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list()
                historicActivityInstanceList.forEach { activityIdList.add(it.activityId) }
                val flows: MutableList<String> = mutableListOf()
                //获取流程图
                val model = repositoryService.getBpmnModel(processDefinitionId)
                val engineConfiguration = processEngine.processEngineConfiguration
                val diagramGenerator = engineConfiguration.processDiagramGenerator
                diagramGenerator.generateDiagram(model, "bmp", activityIdList, flows,
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

}
