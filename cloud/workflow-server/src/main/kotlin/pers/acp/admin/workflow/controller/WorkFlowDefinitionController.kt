package pers.acp.admin.workflow.controller

import io.swagger.annotations.*
import org.bouncycastle.util.encoders.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pers.acp.admin.api.WorkFlowApi
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.workflow.constant.WorkFlowExpression
import pers.acp.admin.workflow.domain.WorkFlowDefinitionDomain
import pers.acp.admin.workflow.entity.WorkFlowDefinition
import pers.acp.admin.workflow.po.WorkFlowDefinitionPo
import pers.acp.admin.workflow.po.WorkFlowDefinitionQueryPo
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(WorkFlowApi.basePath)
@Api(tags = ["工作流部署"])
class WorkFlowDefinitionController @Autowired
constructor(private val logAdapter: LogAdapter,
            private val workFlowDefinitionDomain: WorkFlowDefinitionDomain) : BaseController() {

    @ApiOperation(value = "新建工作流信息")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = WorkFlowDefinition::class), ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowDefinition)
    @PostMapping(value = [WorkFlowApi.definitionFile], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun create(@ApiParam(value = "流程配置文件", required = true)
               @NotNull(message = "流程配置文件不能为空")
               @RequestParam file: MultipartFile,
               @ApiParam(value = "备注", required = true)
               @RequestParam(required = false) remarks: String?): ResponseEntity<WorkFlowDefinition> {
        if (file.isEmpty) {
            throw ServerException("请选择流程配置文件")
        }
        return WorkFlowDefinitionPo(remarks = remarks).let { po ->
            workFlowDefinitionDomain.doCreate(po, file).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }
        }
    }

    @ApiOperation(value = "删除指定的工作流信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowDefinition)
    @DeleteMapping(value = [WorkFlowApi.definition], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun delete(@ApiParam(value = "id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: MutableList<String>): ResponseEntity<InfoVo> =
            workFlowDefinitionDomain.doDelete(idList).let {
                ResponseEntity.ok(InfoVo(message = "删除成功"))
            }

    @ApiOperation(value = "更新指定的工作流信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；配置ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowDefinition)
    @PatchMapping(value = [WorkFlowApi.definition], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun update(@RequestBody @Valid workFlowDefinitionPo: WorkFlowDefinitionPo): ResponseEntity<WorkFlowDefinition> {
        if (CommonTools.isNullStr(workFlowDefinitionPo.id)) {
            throw ServerException("ID不能为空")
        }
        return workFlowDefinitionDomain.doUpdate(workFlowDefinitionPo).let {
            ResponseEntity.ok(it)
        }
    }

    @ApiOperation(value = "查询工作流信息列表")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowDefinition)
    @PostMapping(value = [WorkFlowApi.definition], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun query(@RequestBody @Valid workFlowDefinitionQueryPo: WorkFlowDefinitionQueryPo): ResponseEntity<Page<WorkFlowDefinition>> =
            ResponseEntity.ok(workFlowDefinitionDomain.doQuery(workFlowDefinitionQueryPo))

    @ApiOperation(value = "部署工作流")
    @ApiResponses(ApiResponse(code = 201, message = "部署成功", response = WorkFlowDefinition::class), ApiResponse(code = 400, message = "流程部署失败；", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowDefinition)
    @PutMapping(value = [WorkFlowApi.definitionDeploy + "/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun deploy(@PathVariable id: String): ResponseEntity<WorkFlowDefinition> =
            ResponseEntity.status(HttpStatus.CREATED).body(workFlowDefinitionDomain.doDeploy(id))

    @ApiOperation(value = "流程定义文件下载")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @GetMapping(value = [WorkFlowApi.definitionFile + "/{id}"], produces = [MediaType.ALL_VALUE])
    @Throws(ServerException::class)
    fun downloadFile(request: HttpServletRequest, response: HttpServletResponse,
                     @PathVariable id: String) {
        workFlowDefinitionDomain.doDownLoadFile(request, response, id)
    }

    @ApiOperation(value = "获取流程图", notes = "返回图片二进制流数据")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；系统异常", response = ErrorVo::class))
    @PreAuthorize(WorkFlowExpression.flowDefinition)
    @GetMapping(value = [WorkFlowApi.definitionDiagram + "/{deploymentId}/{imgType}"], produces = [MediaType.ALL_VALUE])
    @Throws(ServerException::class)
    fun diagram(@ApiParam(value = "流程部署ID", required = true)
                @PathVariable
                deploymentId: String,
                @ApiParam(value = "图片格式", example = "png;bmp", required = true)
                @PathVariable
                imgType: String): ResponseEntity<String> =
            workFlowDefinitionDomain.generateDefinitionDiagram(deploymentId, imgType).let { inputStream ->
                var out: ByteArrayOutputStream? = null
                try {
                    out = ByteArrayOutputStream()
                    val buffImg = ImageIO.read(inputStream)
                    ImageIO.write(buffImg, imgType, out)
                    ResponseEntity.ok("data:image/$imgType;base64," + Base64.toBase64String(out.toByteArray()))
                } finally {
                    try {
                        out?.close()
                        inputStream.close()
                    } catch (ex: Exception) {
                        logAdapter.error(ex.message, ex)
                    }
                }
            }
}
