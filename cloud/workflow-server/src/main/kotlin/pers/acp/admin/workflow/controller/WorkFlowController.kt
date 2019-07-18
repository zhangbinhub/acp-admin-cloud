package pers.acp.admin.workflow.controller

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.annotation.DuplicateSubmission
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.vo.FlowHistoryVO
import pers.acp.admin.common.vo.InfoVO
import pers.acp.admin.workflow.constant.WorkFlowApi
import pers.acp.admin.workflow.constant.WorkFlowExpression
import pers.acp.admin.workflow.domain.WorkFlowDomain
import pers.acp.admin.common.po.FlowApprovePO
import pers.acp.admin.common.po.FlowStartPO
import pers.acp.admin.common.vo.FlowTaskVO
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.vo.ErrorVO
import pers.acp.spring.cloud.log.LogInstance

import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import java.io.*

/**
 * @author zhang by 10/06/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(WorkFlowApi.basePath)
@Api("工作流控制")
class WorkFlowController @Autowired
constructor(private val logInstance: LogInstance, private val workFlowDomain: WorkFlowDomain) : BaseController() {

    @ApiOperation(value = "启动流程", notes = "启动指定的流程，并关联唯一业务主键")
    @ApiResponses(ApiResponse(code = 201, message = "流程启动成功", response = InfoVO::class), ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO::class))
    @PreAuthorize(WorkFlowExpression.flowStart)
    @PutMapping(value = [WorkFlowApi.flowStart], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun create(@RequestBody @Valid flowStartPO: FlowStartPO): ResponseEntity<InfoVO> =
            workFlowDomain.startFlow(flowStartPO.processDefinitionKey!!, flowStartPO.businessKey!!, flowStartPO.params).let {
                ResponseEntity.status(HttpStatus.CREATED).body(InfoVO(message = it))
            }

    @ApiOperation(value = "获取待办任务", notes = "获取指定用户的待办任务列表")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO::class))
    @PreAuthorize(WorkFlowExpression.flowPending)
    @GetMapping(value = [WorkFlowApi.flowPending + "/{userId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun pending(@ApiParam(value = "用户ID", required = true) @PathVariable userId: String): ResponseEntity<List<FlowTaskVO>> =
            workFlowDomain.findTaskListByUserId(userId).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "流程审批", notes = "可选通过或不通过")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO::class))
    @PreAuthorize(WorkFlowExpression.flowApprove)
    @PostMapping(value = [WorkFlowApi.flowApprove], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun approve(@RequestBody @Valid flowApprovePO: FlowApprovePO): ResponseEntity<InfoVO> {
        if (flowApprovePO.approved!!) {
            workFlowDomain.pass(flowApprovePO.taskId!!, flowApprovePO.comment, flowApprovePO.params, flowApprovePO.taskParams)
        } else {
            workFlowDomain.noPass(flowApprovePO.taskId!!, flowApprovePO.comment, flowApprovePO.params, flowApprovePO.taskParams)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(InfoVO(message = "流程处理完成"))
    }


    @ApiOperation(value = "获取流程历史记录", notes = "获取指定流程实例的历史处理记录")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO::class))
    @PreAuthorize(WorkFlowExpression.flowHistory)
    @GetMapping(value = [WorkFlowApi.flowHistory + "/{processInstanceId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun history(@ApiParam(value = "流程实例id", required = true) @PathVariable processInstanceId: String): ResponseEntity<List<FlowHistoryVO>> =
            workFlowDomain.findHistoryInfo(processInstanceId).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "获取流程图", notes = "获取指定实例id的流程图，返回图片二进制流数据")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVO::class))
    @PreAuthorize(WorkFlowExpression.flowDiagram)
    @GetMapping(value = [WorkFlowApi.flowDiagram + "/{processInstanceId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun diagram(response: HttpServletResponse, @ApiParam(value = "流程实例id", required = true) @PathVariable processInstanceId: String) {
        val inputStream = workFlowDomain.generateDiagram(processInstanceId)
        var outputStream: OutputStream? = null
        try {
            val buffer = ByteArray(inputStream.available())
            if (inputStream.read(buffer) == -1) {
                logInstance.error("图片生成失败，流程引擎生成为空")
                throw ServerException("图片生成失败")
            }
            response.reset()
            outputStream = BufferedOutputStream(response.outputStream)
            outputStream.write(buffer)
            outputStream.flush()
        } catch (e: Exception) {
            logInstance.error(e.message, e)
            throw ServerException("图片生成失败")
        } finally {
            try {
                inputStream.close()
                outputStream?.close()
            } catch (ex: Exception) {
                logInstance.error(ex.message, ex)
            }
        }
    }

}
