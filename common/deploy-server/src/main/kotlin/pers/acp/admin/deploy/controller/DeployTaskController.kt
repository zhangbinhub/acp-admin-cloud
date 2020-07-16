package pers.acp.admin.deploy.controller

import com.fasterxml.jackson.databind.ObjectMapper
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
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.deploy.bus.publish.DeployEventPublish
import pers.acp.admin.deploy.constant.DeployApi
import pers.acp.admin.deploy.domain.DeployTaskDomain
import pers.acp.admin.deploy.entity.DeployTask
import pers.acp.admin.deploy.po.DeployTaskPo
import pers.acp.admin.deploy.po.DeployTaskQueryPo
import pers.acp.admin.deploy.po.FileDownLoadPo
import pers.acp.admin.permission.BaseExpression
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission
import java.io.File
import java.nio.charset.Charset
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import javax.validation.constraints.*

@Validated
@RestController
@RequestMapping(DeployApi.basePath)
@Api(tags = ["部署任务管理"])
class DeployTaskController @Autowired
constructor(logAdapter: LogAdapter,
            private val objectMapper: ObjectMapper,
            private val deployTaskDomain: DeployTaskDomain,
            private val deployEventPublish: DeployEventPublish) : BaseController(logAdapter) {
    @ApiOperation(value = "新建部署任务")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = DeployTask::class), ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PutMapping(value = [DeployApi.task], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun add(@RequestBody @Valid deployTaskPo: DeployTaskPo): ResponseEntity<DeployTask> =
            deployTaskDomain.doCreate(deployTaskPo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "删除指定的部署任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @DeleteMapping(value = [DeployApi.task], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun delete(@ApiParam(value = "id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: MutableList<String>): ResponseEntity<InfoVo> =
            deployTaskDomain.doDelete(idList).let {
                ResponseEntity.ok(InfoVo(message = "删除成功"))
            }

    @ApiOperation(value = "更新指定的部署任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PatchMapping(value = [DeployApi.task], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun update(@RequestBody @Valid deployTaskPo: DeployTaskPo): ResponseEntity<DeployTask> {
        if (CommonTools.isNullStr(deployTaskPo.id)) {
            throw ServerException("ID不能为空")
        }
        return deployTaskDomain.doUpdate(deployTaskPo).let {
            ResponseEntity.ok(it)
        }
    }

    @ApiOperation(value = "查询部署任务列表")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PostMapping(value = [DeployApi.task], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun query(@RequestBody @Valid deployTaskQueryPo: DeployTaskQueryPo): ResponseEntity<Page<DeployTask>> =
            deployTaskQueryPo.let {
                if (it.startTime != null) {
                    it.startTime = longToDate(it.startTime!!).millis
                }
                if (it.endTime != null) {
                    it.endTime = longToDate(it.endTime!!).millis
                }
                ResponseEntity.ok(deployTaskDomain.doQuery(deployTaskQueryPo))
            }

    @ApiOperation(value = "查询服务器文件")
    @PreAuthorize(BaseExpression.superOnly)
    @GetMapping(value = [DeployApi.file], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun queryFile(): ResponseEntity<List<String>> = ResponseEntity.ok(deployTaskDomain.fileList())

    @ApiOperation(value = "上传文件")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PostMapping(value = [DeployApi.file], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun uploadFile(@RequestParam file: MultipartFile): ResponseEntity<InfoVo> {
        if (file.isEmpty) {
            throw ServerException("请选择需要上传的文件")
        }
        deployTaskDomain.uploadFile(file).let {
            return ResponseEntity.ok(InfoVo(message = it))
        }
    }

    @ApiOperation(value = "删除文件")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @DeleteMapping(value = [DeployApi.file], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun deleteFile(@ApiParam(value = "文件名称", required = true)
                   @NotBlank(message = "文件名称不能为空")
                   @RequestParam fileName: String): ResponseEntity<InfoVo> {
        val name = String(Base64.decode(fileName), Charset.forName(CommonTools.getDefaultCharset())).replace("/", File.separator).replace("\\", File.separator)
        return deployTaskDomain.deleteFile(name).let {
            ResponseEntity.ok(InfoVo(message = "文件删除成功"))
        }
    }

    @ApiOperation(value = "下载文件")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @GetMapping(value = [DeployApi.fileDownLoad], produces = [MediaType.ALL_VALUE])
    @Throws(ServerException::class)
    fun downloadFile(request: HttpServletRequest, response: HttpServletResponse,
                     @ApiParam(value = "文件内容", required = true)
                     @NotBlank(message = "文件内容不能为空")
                     @RequestParam params: String) {
        val fileContent = objectMapper.readValue(params, FileDownLoadPo::class.java)
        if (CommonTools.isNullStr(fileContent.fileName)) {
            throw ServerException("文件名称不能为空")
        }
        val fileName = String(Base64.decode(fileContent.fileName), Charset.forName(CommonTools.getDefaultCharset())).replace("/", File.separator).replace("\\", File.separator)
        deployTaskDomain.doDownLoadFile(request, response, fileName)
    }

    @ApiOperation(value = "执行部署任务")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PostMapping(value = [DeployApi.taskExecute + "/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun execute(@PathVariable id: String): ResponseEntity<InfoVo> =
            deployTaskDomain.executeTask(id).let { deployTask ->
                deployEventPublish.doNotifyExecuteDeploy(deployTask.id).let {
                    ResponseEntity.accepted().body(InfoVo(message = "请求成功，稍后将执行部署任务"))
                }
            }
}