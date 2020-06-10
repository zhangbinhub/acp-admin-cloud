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
import pers.acp.admin.common.vo.ProcessTaskVo
import pers.acp.admin.workflow.domain.WorkFlowDomain
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission
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
constructor(logAdapter: LogAdapter,
            private val workFlowDomain: WorkFlowDomain) : BaseController(logAdapter) {

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
}
