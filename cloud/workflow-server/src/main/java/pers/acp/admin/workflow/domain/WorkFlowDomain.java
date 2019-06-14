package pers.acp.admin.workflow.domain;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.*;
import org.flowable.engine.history.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.base.BaseDomain;
import pers.acp.admin.common.vo.FlowHistoryVO;
import pers.acp.admin.common.vo.FlowTaskVO;
import pers.acp.admin.workflow.constant.WorkFlowParamKey;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springcloud.common.log.LogInstance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhang by 10/06/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class WorkFlowDomain extends BaseDomain {

    private final LogInstance logInstance;

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final RepositoryService repositoryService;

    private final HistoryService historyService;

    private final ProcessEngine processEngine;

    @Autowired
    public WorkFlowDomain(LogInstance logInstance, RuntimeService runtimeService, TaskService taskService, RepositoryService repositoryService, HistoryService historyService, @Qualifier("processEngine") ProcessEngine processEngine) {
        this.logInstance = logInstance;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.historyService = historyService;
        this.processEngine = processEngine;
    }

    /**
     * 任务实体转换
     *
     * @param task 任务对象
     * @return 转换后任务对象
     */
    private FlowTaskVO taskToVO(TaskInfo task) {
        FlowTaskVO flowTaskVO = new FlowTaskVO();
        flowTaskVO.setProcessInstanceId(task.getProcessInstanceId());
        flowTaskVO.setName(task.getName());
        flowTaskVO.setTaskId(task.getId());
        flowTaskVO.setParentTaskId(task.getParentTaskId());
        flowTaskVO.setExecutionId(task.getExecutionId());
        flowTaskVO.setBusinessKey(runtimeService.getVariable(task.getExecutionId(), WorkFlowParamKey.businessKey, String.class));
        flowTaskVO.setParams(runtimeService.getVariables(task.getExecutionId()));
        flowTaskVO.setUserId(task.getAssignee());
        flowTaskVO.setLocalParams(task.getTaskLocalVariables());
        flowTaskVO.setCreateTime(task.getCreateTime().getTime());
        return flowTaskVO;
    }

    /**
     * 历史记录实例转换
     *
     * @param historicActivityInstance 历史记录
     * @return 转换后对象
     */
    private FlowHistoryVO actToVO(HistoricActivityInstance historicActivityInstance, String businessKey) {
        FlowHistoryVO flowHistoryVO = new FlowHistoryVO();
        HistoricDetailQuery historicDetailQuery = historyService.createHistoricDetailQuery().activityInstanceId(historicActivityInstance.getId());
        Map<String, Object> params = historicDetailQuery.list().stream().filter(historicDetail -> CommonTools.isNullStr(historicDetail.getTaskId()))
                .collect(Collectors.toMap(historicDetail -> ((HistoricVariableUpdate) historicDetail).getVariableName(), historicDetail -> ((HistoricVariableUpdate) historicDetail).getValue()));
        flowHistoryVO.setProcessInstanceId(historicActivityInstance.getProcessInstanceId());
        flowHistoryVO.setActivityId(historicActivityInstance.getActivityId());
        flowHistoryVO.setActivityName(historicActivityInstance.getActivityName());
        flowHistoryVO.setTaskId(historicActivityInstance.getTaskId());
        flowHistoryVO.setExecutionId(historicActivityInstance.getExecutionId());
        flowHistoryVO.setBusinessKey(businessKey);
        flowHistoryVO.setUserId(historicActivityInstance.getAssignee());
        flowHistoryVO.setApproved((Boolean) params.get(WorkFlowParamKey.approved));
        flowHistoryVO.setComment(params.get(WorkFlowParamKey.comment).toString());
        flowHistoryVO.setParams(params);
        flowHistoryVO.setLocalParams(historicDetailQuery.taskId(historicActivityInstance.getTaskId()).list().stream()
                .collect(Collectors.toMap(historicDetail -> ((HistoricVariableUpdate) historicDetail).getVariableName(), historicDetail -> ((HistoricVariableUpdate) historicDetail).getValue())));
        flowHistoryVO.setCreateTime(historicActivityInstance.getStartTime().getTime());
        flowHistoryVO.setEndTime(historicActivityInstance.getEndTime().getTime());
        return flowHistoryVO;
    }

    /**
     * 启动流程
     *
     * @param processDefinitionKey 流程键
     * @param businessKey          业务键
     * @return 流程实例id
     */
    @Transactional
    public String startFlow(String processDefinitionKey, String businessKey, Map<String, Object> params) throws ServerException {
        params.put(WorkFlowParamKey.businessKey, businessKey);
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, params);
            return processInstance.getId();
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 获取用户待办任务
     *
     * @param userId 用户id
     * @return 任务列表
     */
    public List<FlowTaskVO> findTaskListByUserId(String userId) throws ServerException {
        try {
            return taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list()
                    .stream().map(this::taskToVO).collect(Collectors.toList());
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
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
    public void pass(String taskId, String comment, Map<String, Object> params, Map<String, Object> taskParams) throws ServerException {
        if (CommonTools.isNullStr(comment)) {
            comment = "审批通过";
        }
        approved(taskId, true, comment, params, taskParams);
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
    public void noPass(String taskId, String comment, Map<String, Object> params, Map<String, Object> taskParams) throws ServerException {
        if (CommonTools.isNullStr(comment)) {
            comment = "审批不通过";
        }
        approved(taskId, false, comment, params, taskParams);
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
    private void approved(String taskId, boolean approved, String comment, Map<String, Object> params, Map<String, Object> taskParams) throws ServerException {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                logInstance.error("流程任务【" + taskId + "】不存在！");
                throw new ServerException("流程任务不存在！");
            }
            //通过审核
            HashMap<String, Object> map = new HashMap<>();
            map.put(WorkFlowParamKey.approved, approved);
            map.put(WorkFlowParamKey.comment, comment);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (!map.containsKey(entry.getKey())) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
            runtimeService.setVariablesLocal(task.getExecutionId(), taskParams);
            taskService.complete(taskId, map);
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 查询指定流程的历史信息
     *
     * @param processInstanceId 流程id，为空或null表示查询所有
     * @return 流程历史信息
     */
    public List<FlowHistoryVO> findHistoryInfo(String processInstanceId) throws ServerException {
        try {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (historicProcessInstance == null) {
                logInstance.error("流程实例【" + processInstanceId + "】不存在");
                throw new ServerException("流程实例不存在！");
            }
            return historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).finished()
                    .orderByHistoricActivityInstanceEndTime().asc().list().stream()
                    .filter(historicActivityInstance -> !CommonTools.isNullStr(historicActivityInstance.getTaskId()))
                    .map(historicActivityInstance -> actToVO(historicActivityInstance, historicProcessInstance.getBusinessKey())).collect(Collectors.toList());
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 获取下一步流程节点列表
     *
     * @param taskId 当前任务id
     * @return 流程节点列表
     * @throws ServerException 异常
     */
    public List<FlowElement> getNextFlowElementList(String taskId) throws ServerException {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                logInstance.error("流程任务【" + taskId + "】不存在！");
                throw new ServerException("流程任务不存在！");
            }
            Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
            FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(execution.getActivityId());
            List<SequenceFlow> outFlows = flowNode.getOutgoingFlows();
            return outFlows.stream().map(SequenceFlow::getTargetFlowElement).collect(Collectors.toList());
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 生成流程图
     *
     * @param processInstanceId 流程实例id
     * @return 流程图输入流
     * @throws ServerException 异常
     */
    public InputStream generateDiagram(String processInstanceId) throws ServerException {
        try {
            String processDefinitionId;
            if (isFinished(processInstanceId)) {
                HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                processDefinitionId = pi.getProcessDefinitionId();
            } else {
                ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                processDefinitionId = pi.getProcessDefinitionId();
            }
            List<String> activityIdList = new ArrayList<>();
            List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                activityIdList.add(activityInstance.getActivityId());
            }
            List<String> flows = new ArrayList<>();
            //获取流程图
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            ProcessEngineConfiguration engineConfiguration = processEngine.getProcessEngineConfiguration();
            ProcessDiagramGenerator diagramGenerator = engineConfiguration.getProcessDiagramGenerator();
            return diagramGenerator.generateDiagram(bpmnModel, "bmp", activityIdList, flows,
                    engineConfiguration.getActivityFontName(), engineConfiguration.getLabelFontName(), engineConfiguration.getAnnotationFontName(),
                    engineConfiguration.getClassLoader(), 1.0, true);
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 流程是否结束
     *
     * @param processInstanceId 流程实例id
     * @return true|false
     */
    public boolean isFinished(String processInstanceId) {
        return historyService.createHistoricProcessInstanceQuery().finished().processInstanceId(processInstanceId).count() > 0;
    }

}
