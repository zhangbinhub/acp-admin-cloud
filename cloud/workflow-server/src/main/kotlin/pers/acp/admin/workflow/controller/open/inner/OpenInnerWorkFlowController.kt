package pers.acp.admin.workflow.controller.open.inner

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.common.po.ProcessStartPo
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.api.WorkFlowApi
import pers.acp.admin.common.po.ProcessHandlingPo
import pers.acp.admin.common.po.ProcessTerminationPo
import pers.acp.admin.common.vo.ProcessInstanceVo
import pers.acp.admin.common.vo.ProcessTaskVo
import pers.acp.admin.workflow.domain.WorkFlowDomain
import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.vo.ErrorVo
import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudDuplicateSubmission
import javax.validation.Valid

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.openInnerBasePath)
@Api(tags = ["工作流引擎（内部开放接口）"])
class OpenInnerWorkFlowController @Autowired
constructor(private val logAdapter: LogAdapter,
            private val workFlowDomain: WorkFlowDomain) : BaseController(logAdapter) {
    @ApiOperation(value = "获取流程任务信息", notes = "获取指定流程任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @GetMapping(value = [WorkFlowApi.task + "/{taskId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun queryTaskInfo(@ApiParam(value = "流程任务ID", required = true)
                      @PathVariable taskId: String): ResponseEntity<ProcessTaskVo> =
            workFlowDomain.findTaskByIdOpen(taskId).let {
                ResponseEntity.ok(it)
            }

    @ApiOperation(value = "启动流程", notes = "启动指定的流程，并关联唯一业务主键")
    @ApiResponses(ApiResponse(code = 201, message = "流程启动成功", response = InfoVo::class), ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PutMapping(value = [WorkFlowApi.start], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun start(@RequestBody @Valid processStartPo: ProcessStartPo): ResponseEntity<InfoVo> =
            workFlowDomain.startFlow(processStartPo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(InfoVo(message = it))
            }

    @ApiOperation(value = "启动流程", notes = "启动指定的流程，并关联唯一业务主键")
    @ApiResponses(ApiResponse(code = 201, message = "流程启动成功", response = InfoVo::class), ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PutMapping(value = [WorkFlowApi.start + "/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun startByUser(@PathVariable userId: String, @RequestBody @Valid processStartPo: ProcessStartPo): ResponseEntity<InfoVo> =
            workFlowDomain.startFlow(processStartPo, userId).let {
                ResponseEntity.status(HttpStatus.CREATED).body(InfoVo(message = it))
            }

    @ApiOperation(value = "流程处理", notes = "可选通过或不通过")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PostMapping(value = [WorkFlowApi.process + "/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun processByUser(@PathVariable userId: String, @RequestBody @Valid processHandlingPo: ProcessHandlingPo): ResponseEntity<InfoVo> =
            workFlowDomain.processTask(processHandlingPo, userId).let {
                ResponseEntity.ok(InfoVo(message = "流程处理完成"))
            }

    @ApiOperation(value = "查询待办任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @GetMapping(value = [WorkFlowApi.pending + "/{processInstanceId}/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun pendingByUser(@PathVariable processInstanceId: String, @PathVariable userId: String): ResponseEntity<List<ProcessTaskVo>> =
            workFlowDomain.findTaskList(processInstanceId, userId).let {
                ResponseEntity.ok(it)
            }

    @ApiOperation(value = "流程强制结束")
    @ApiResponses(ApiResponse(code = 400, message = "系统异常", response = ErrorVo::class))
    @DeleteMapping(value = [WorkFlowApi.termination], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun termination(@RequestBody @Valid processTerminationPo: ProcessTerminationPo): ResponseEntity<InfoVo> =
            workFlowDomain.findProcessInstance(processTerminationPo.processInstanceId!!).let { instance ->
                if (instance.finished) {
                    logAdapter.warn("流程已结束，无法再次终止该流程！")
                }
                workFlowDomain.deleteProcessInstance(processTerminationPo).let {
                    ResponseEntity.ok(InfoVo(message = "强制结束流程实例成功"))
                }
            }

    @ApiOperation(value = "获取流程实例", notes = "获取指定流程实例")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @GetMapping(value = [WorkFlowApi.instance + "/{processInstanceId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun queryInstance(@ApiParam(value = "流程实例id", required = true)
                      @PathVariable
                      processInstanceId: String): ResponseEntity<ProcessInstanceVo> =
            workFlowDomain.findProcessInstance(processInstanceId).let {
                ResponseEntity.ok(it)
            }
}
