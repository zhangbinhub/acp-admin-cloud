package pers.acp.admin.workflow.controller

import io.swagger.annotations.*
import org.bouncycastle.util.encoders.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.vo.*
import pers.acp.admin.api.WorkFlowApi
import pers.acp.admin.common.po.*
import pers.acp.admin.workflow.constant.WorkFlowExpression
import pers.acp.admin.workflow.domain.WorkFlowDomain
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission
import javax.validation.Valid

/**
 * @author zhang by 10/06/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(WorkFlowApi.basePath)
@Api(tags = ["工作流引擎"])
class WorkFlowController @Autowired
constructor(private val logAdapter: LogAdapter,
            private val workFlowDomain: WorkFlowDomain) : BaseController() {
    @ApiOperation(value = "启动流程", notes = "启动指定的流程，并关联唯一业务主键")
    @ApiResponses(ApiResponse(code = 201, message = "流程启动成功", response = InfoVo::class), ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PutMapping(value = [WorkFlowApi.start], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun start(@RequestBody @Valid processStartPo: ProcessStartPo): ResponseEntity<InfoVo> =
            workFlowDomain.startFlow(processStartPo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(InfoVo(message = it))
            }

    @ApiOperation(value = "查询待办任务", notes = "获取当前用户的待办任务列表")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowPending)
    @PostMapping(value = [WorkFlowApi.pending], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun pending(@RequestBody @Valid processQueryPo: ProcessQueryPo): ResponseEntity<CustomerQueryPageVo<ProcessTaskVo>> =
            workFlowDomain.findTaskList(processQueryPo).let {
                ResponseEntity.ok(it)
            }

    @ApiOperation(value = "领取任务", notes = "签收指定的任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowProcess)
    @PatchMapping(value = [WorkFlowApi.claim + "/{taskId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun claim(@ApiParam(value = "任务ID", required = true) @PathVariable taskId: String): ResponseEntity<InfoVo> =
            workFlowDomain.claimTask(taskId).let {
                ResponseEntity.ok(InfoVo(message = "任务已签收"))
            }

    @ApiOperation(value = "转办任务", notes = "转办指定的任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowProcess)
    @PatchMapping(value = [WorkFlowApi.transfer + "/{taskId}/{userId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun transfer(@ApiParam(value = "任务ID", required = true) @PathVariable taskId: String,
                 @ApiParam(value = "目标userId", required = true) @PathVariable userId: String): ResponseEntity<InfoVo> =
            workFlowDomain.turnTask(taskId, userId).let {
                ResponseEntity.ok(InfoVo(message = "任务已转办"))
            }

    @ApiOperation(value = "委托办理任务", notes = "委托办理指定的任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowProcess)
    @PatchMapping(value = [WorkFlowApi.delegate + "/{taskId}/{acceptUserId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun delegate(@ApiParam(value = "任务ID", required = true) @PathVariable taskId: String,
                 @ApiParam(value = "接收userId", required = true) @PathVariable acceptUserId: String): ResponseEntity<InfoVo> =
            workFlowDomain.delegateTask(taskId, acceptUserId).let {
                ResponseEntity.ok(InfoVo(message = "任务已委托办理"))
            }

    @ApiOperation(value = "流程处理", notes = "可选通过或不通过")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowProcess)
    @PostMapping(value = [WorkFlowApi.process], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun process(@RequestBody @Valid processHandlingPo: ProcessHandlingPo): ResponseEntity<InfoVo> =
            workFlowDomain.processTask(processHandlingPo).let {
                ResponseEntity.ok(InfoVo(message = "流程处理完成"))
            }

    @ApiOperation(value = "流程强制结束")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowAdmin)
    @PostMapping(value = [WorkFlowApi.termination], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun delete(@RequestBody @Valid processTerminationPo: ProcessTerminationPo): ResponseEntity<InfoVo> =
            workFlowDomain.deleteProcessInstance(processTerminationPo).let {
                ResponseEntity.ok(InfoVo(message = "强制结束流程实例成功"))
            }

    @ApiOperation(value = "获取流程实例", notes = "获取指定流程实例")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowManage)
    @GetMapping(value = [WorkFlowApi.instance + "/{processInstanceId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun queryInstance(@ApiParam(value = "流程实例id", required = true)
                      @PathVariable
                      processInstanceId: String): ResponseEntity<ProcessInstanceVo> =
            workFlowDomain.findProcessInstance(processInstanceId).let {
                ResponseEntity.ok(it)
            }

    @ApiOperation(value = "获取流程实例", notes = "获取流程实例")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowManage)
    @PostMapping(value = [WorkFlowApi.instance], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun queryInstance(@RequestBody @Valid processQueryPo: ProcessQueryPo): ResponseEntity<CustomerQueryPageVo<ProcessInstanceVo>> =
            workFlowDomain.findProcessInstance(processQueryPo).let {
                ResponseEntity.ok(it)
            }

    @ApiOperation(value = "获取流程实例", notes = "获取流程实例")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowHistory)
    @PostMapping(value = [WorkFlowApi.history], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun queryHistoryInstance(@RequestBody @Valid processQueryPo: ProcessQueryPo): ResponseEntity<CustomerQueryPageVo<ProcessInstanceVo>> =
            workFlowDomain.findHistoryProcessInstance(processQueryPo).let {
                ResponseEntity.ok(it)
            }

    @ApiOperation(value = "获取流程历史记录", notes = "获取指定流程实例的历史处理记录")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowHistory)
    @GetMapping(value = [WorkFlowApi.history + "/{processInstanceId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun queryHistoryActivity(@ApiParam(value = "流程实例id", required = true)
                             @PathVariable
                             processInstanceId: String): ResponseEntity<List<ProcessHistoryActivityVo>> =
            workFlowDomain.findHistoryActivity(processInstanceId).let {
                ResponseEntity.ok(it)
            }

    @ApiOperation(value = "获取流程任务信息", notes = "获取指定流程任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowManage)
    @GetMapping(value = [WorkFlowApi.task + "/{taskId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun queryTaskInfo(@ApiParam(value = "流程任务ID", required = true)
                      @PathVariable
                      taskId: String): ResponseEntity<ProcessTaskVo> =
            workFlowDomain.findTaskId(taskId).let {
                ResponseEntity.ok(it)
            }

    @ApiOperation(value = "获取流程图", notes = "获取指定实例id的流程图，返回图片二进制流数据")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowDiagram)
    @GetMapping(value = [WorkFlowApi.diagram + "/{processInstanceId}/{imgType}"], produces = [MediaType.ALL_VALUE])
    @Throws(ServerException::class)
    fun diagram(@ApiParam(value = "流程实例id", required = true)
                @PathVariable
                processInstanceId: String,
                @ApiParam(value = "图片格式", example = "png;bmp", required = true)
                @PathVariable
                imgType: String): ResponseEntity<String> =
            workFlowDomain.generateDiagram(processInstanceId, imgType).let { inputStream ->
                try {
                    val bytes = ByteArray(inputStream.available())
                    if (inputStream.read(bytes) == -1) {
                        logAdapter.error("图片生成失败，流程引擎生成为空")
                        throw ServerException("图片生成失败")
                    }
                    ResponseEntity.ok("data:image/$imgType;base64," + Base64.toBase64String(bytes))
                } finally {
                    try {
                        inputStream.close()
                    } catch (ex: Exception) {
                        logAdapter.error(ex.message, ex)
                    }
                }
            }
}
