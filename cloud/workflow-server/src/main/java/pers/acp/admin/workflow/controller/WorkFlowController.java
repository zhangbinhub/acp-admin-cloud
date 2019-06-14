package pers.acp.admin.workflow.controller;

import io.swagger.annotations.*;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.annotation.DuplicateSubmission;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.workflow.constant.WorkFlowApi;
import pers.acp.admin.workflow.constant.WorkFlowExpression;
import pers.acp.admin.workflow.constant.WorkFlowParamKey;
import pers.acp.admin.workflow.domain.WorkFlowDomain;
import pers.acp.admin.workflow.po.FlowApprovePO;
import pers.acp.admin.workflow.po.FlowStartPO;
import pers.acp.admin.common.vo.FlowTaskVO;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;
import pers.acp.springcloud.common.log.LogInstance;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhang by 10/06/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(WorkFlowApi.basePath)
@Api("工作流控制")
public class WorkFlowController extends BaseController {

    private final LogInstance logInstance;

    private final WorkFlowDomain workFlowDomain;

    @Autowired
    public WorkFlowController(LogInstance logInstance, WorkFlowDomain workFlowDomain) {
        this.logInstance = logInstance;
        this.workFlowDomain = workFlowDomain;
    }

    @ApiOperation(value = "启动流程", notes = "启动指定的流程，并关联唯一业务主键")
    @ApiResponses({
            @ApiResponse(code = 201, message = "流程启动成功", response = InfoVO.class),
            @ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO.class)
    })
    @PreAuthorize(WorkFlowExpression.flowStart)
    @PutMapping(value = WorkFlowApi.flowStart, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @DuplicateSubmission
    public ResponseEntity<InfoVO> create(@RequestBody @Valid FlowStartPO flowStartPO) throws ServerException {
        String processInstanceId = workFlowDomain.startFlow(flowStartPO.getProcessDefinitionKey(), flowStartPO.getBusinessKey(), flowStartPO.getParams());
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage(processInstanceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(infoVO);
    }

    @ApiOperation(value = "获取待办任务", notes = "获取指定用户的待办任务列表")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO.class)
    })
    @PreAuthorize(WorkFlowExpression.flowPending)
    @GetMapping(value = WorkFlowApi.flowPending + "/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @DuplicateSubmission
    public ResponseEntity<List<FlowTaskVO>> pending(@ApiParam(value = "用户ID", required = true) @PathVariable String userId) throws ServerException {
        List<Task> taskList = workFlowDomain.findTaskListByUserId(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                taskList.stream().map(this::taskToVO).collect(Collectors.toList())
        );
    }

    @ApiOperation(value = "流程审批", notes = "可选通过或不通过")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO.class)
    })
    @PreAuthorize(WorkFlowExpression.flowApprove)
    @PostMapping(value = WorkFlowApi.flowApprove, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @DuplicateSubmission
    public ResponseEntity<InfoVO> approve(@RequestBody @Valid FlowApprovePO flowApprovePO) throws ServerException {
        if (flowApprovePO.getApproved()) {
            workFlowDomain.pass(flowApprovePO.getTaskId(), flowApprovePO.getComment(), flowApprovePO.getParams());
        } else {
            workFlowDomain.noPass(flowApprovePO.getTaskId(), flowApprovePO.getComment(), flowApprovePO.getParams());
        }
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("流程处理完成");
        return ResponseEntity.status(HttpStatus.CREATED).body(infoVO);
    }


    @ApiOperation(value = "获取流程历史记录", notes = "获取指定流程实例的历史处理记录")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO.class)
    })
    @PreAuthorize(WorkFlowExpression.flowHistory)
    @GetMapping(value = WorkFlowApi.flowHistory + "/{processInstanceId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @DuplicateSubmission
    public ResponseEntity<List<FlowTaskVO>> history(@ApiParam(value = "流程实例id", required = true) @PathVariable String processInstanceId) throws ServerException {
        List<HistoricTaskInstance> taskList = workFlowDomain.findHistoryInfo(processInstanceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                taskList.stream().map(task -> {
                    FlowTaskVO flowTaskVO = taskToVO(task);
                    flowTaskVO.setEndTime(task.getEndTime().getTime());
                    return flowTaskVO;
                }).collect(Collectors.toList())
        );
    }

    @ApiOperation(value = "获取流程图", notes = "获取指定实例id的流程图")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO.class)
    })
    @PreAuthorize(WorkFlowExpression.flowDiagram)
    @GetMapping(value = WorkFlowApi.flowDiagram + "/{processInstanceId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @DuplicateSubmission
    public void diagram(HttpServletResponse response, @ApiParam(value = "流程实例id", required = true) @PathVariable String processInstanceId) throws ServerException {
        InputStream inputStream = workFlowDomain.generateDiagram(processInstanceId);
        OutputStream outputStream = null;
        try {
            byte[] buffer = new byte[inputStream.available()];
            if (inputStream.read(buffer) == -1) {
                logInstance.error("图片生成失败，流程引擎生成为空");
                throw new ServerException("图片生成失败");
            }
            inputStream.close();
            response.reset();
            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
            throw new ServerException("图片生成失败");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logInstance.error(e.getMessage(), e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logInstance.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 任务实体转换
     *
     * @param task 原生任务对象
     * @return 转换后任务对象
     */
    private FlowTaskVO taskToVO(TaskInfo task) {
        FlowTaskVO flowTaskVO = new FlowTaskVO();
        flowTaskVO.setProcessInstanceId(task.getProcessInstanceId());
        flowTaskVO.setName(task.getName());
        flowTaskVO.setTaskId(task.getId());
        flowTaskVO.setParentTaskId(task.getParentTaskId());
        flowTaskVO.setExecutionId(task.getExecutionId());
        flowTaskVO.setBusinessKey(task.getProcessVariables().get(WorkFlowParamKey.businessKey).toString());
        flowTaskVO.setUserId(task.getAssignee());
        flowTaskVO.setParams(task.getProcessVariables());
        flowTaskVO.setLocalParams(task.getTaskLocalVariables());
        flowTaskVO.setCreateTime(task.getCreateTime().getTime());
        return flowTaskVO;
    }

}
