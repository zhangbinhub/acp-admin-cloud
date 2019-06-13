package pers.acp.admin.workflow.domain;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.base.BaseDomain;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springcloud.common.log.LogInstance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final ProcessEngineFactoryBean processEngineFactoryBean;

    @Autowired
    public WorkFlowDomain(LogInstance logInstance, RuntimeService runtimeService, TaskService taskService, RepositoryService repositoryService, HistoryService historyService, ProcessEngineFactoryBean processEngineFactoryBean) {
        this.logInstance = logInstance;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.historyService = historyService;
        this.processEngineFactoryBean = processEngineFactoryBean;
    }

    /**
     * 启动流程
     *
     * @param workFlowKey 流程名称
     * @param businessKey 业务键
     * @return 流程实例id
     */
    public String startFlow(String workFlowKey, String businessKey, Map<String, Object> params) throws ServerException {
        if (!params.containsKey("businessKey")) {
            params.put("businessKey", businessKey);
        }
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(workFlowKey, businessKey, params);
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
    public List<Task> findTaskListByUserId(String userId) throws ServerException {
        try {
            return taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 审批通过
     *
     * @param taskId  任务id
     * @param comment 审批意见
     * @throws ServerException 异常
     */
    public void pass(String taskId, String comment) throws ServerException {
        if (CommonTools.isNullStr(comment)) {
            comment = "审批通过";
        }
        approved(taskId, true, comment);
    }

    /**
     * 审批不通过
     *
     * @param taskId  任务id
     * @param comment 审批意见
     * @throws ServerException 异常
     */
    public void noPass(String taskId, String comment) throws ServerException {
        if (CommonTools.isNullStr(comment)) {
            comment = "审批不通过";
        }
        approved(taskId, false, comment);
    }

    /**
     * 审批处理
     *
     * @param taskId   任务id
     * @param approved 审批结果（true-通过，false-不通过）
     * @param comment  审批意见
     * @throws ServerException 异常
     */
    private void approved(String taskId, boolean approved, String comment) throws ServerException {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                logInstance.error("流程【" + taskId + "】不存在！");
                throw new ServerException("流程不存在！");
            }
            //通过审核
            HashMap<String, Object> map = new HashMap<>();
            map.put("approved", approved);
            map.put("comment", comment);
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
    public List<HistoricTaskInstance> findHistoryInfo(String processInstanceId) throws ServerException {
        try {
            return historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).finished()
                    .orderByHistoricTaskInstanceEndTime().asc().list();
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 生成流程图
     *
     * @param taskId 任务名称
     * @return 流程图输入流
     * @throws ServerException 异常
     */
    public InputStream generateDiagram(String taskId) throws ServerException {
        try {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task == null) {
                logInstance.error("流程【" + taskId + "】不存在！");
                throw new ServerException("流程不存在！");
            }
            List<Execution> executions = runtimeService.createExecutionQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .list();
            //得到正在执行的Activity的Id
            List<String> activityIds = new ArrayList<>();
            List<String> flows = new ArrayList<>();
            for (Execution exe : executions) {
                List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
                activityIds.addAll(ids);
            }
            //获取流程图
            BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
            ProcessEngineConfiguration engineConfiguration = processEngineFactoryBean.getProcessEngineConfiguration();
            ProcessDiagramGenerator diagramGenerator = engineConfiguration.getProcessDiagramGenerator();
            return diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows,
                    engineConfiguration.getActivityFontName(), engineConfiguration.getLabelFontName(), engineConfiguration.getAnnotationFontName(),
                    engineConfiguration.getClassLoader(), 1.0, true);
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException(e.getMessage());
        }
    }

}
