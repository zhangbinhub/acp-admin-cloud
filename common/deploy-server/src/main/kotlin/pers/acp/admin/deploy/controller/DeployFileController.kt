package pers.acp.admin.deploy.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.*
import org.bouncycastle.util.encoders.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.deploy.constant.DeployApi
import pers.acp.admin.deploy.po.FilePo
import pers.acp.admin.permission.BaseExpression
import pers.acp.core.CommonTools
import pers.acp.admin.deploy.domain.DeployFileDomain
import pers.acp.admin.deploy.vo.FileVo
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.vo.ErrorVo
import java.nio.charset.Charset
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import javax.validation.constraints.*

@Validated
@RestController
@RequestMapping(DeployApi.basePath)
@Api(tags = ["文件管理"])
class DeployFileController @Autowired
constructor(
    logAdapter: LogAdapter,
    private val objectMapper: ObjectMapper,
    private val deployFileDomain: DeployFileDomain
) : BaseController(logAdapter) {
    @ApiOperation(value = "创建文件夹")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PutMapping(value = [DeployApi.file], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun createFold(@RequestBody @Valid filePo: FilePo): ResponseEntity<FileVo> {
        return deployFileDomain.createFold(filePo.path ?: "", filePo.name!!).let {
            ResponseEntity.ok(it)
        }
    }

    @ApiOperation(value = "查询服务器文件")
    @PreAuthorize(BaseExpression.superOnly)
    @GetMapping(value = [DeployApi.file], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun queryFile(
        @ApiParam(value = "路径", required = false)
        @RequestParam(required = false) path: String?
    ): ResponseEntity<List<FileVo>> = (path?.let {
        String(Base64.decode(path), Charset.forName(CommonTools.getDefaultCharset()))
    } ?: "").let { basePath ->
        ResponseEntity.ok(deployFileDomain.fileList(basePath))
    }

    @ApiOperation(value = "上传文件")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PostMapping(value = [DeployApi.file], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun uploadFile(
        @RequestPart(value = "路径", required = false)
        @RequestParam(required = false) path: String?,
        @RequestPart(value = "文件", required = true)
        @RequestParam file: MultipartFile
    ): ResponseEntity<FileVo> {
        if (file.isEmpty) {
            throw ServerException("请选择需要上传的文件")
        }
        deployFileDomain.uploadFile(path ?: "", file).let {
            return ResponseEntity.ok(it)
        }
    }

    @ApiOperation(value = "删除文件")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @DeleteMapping(value = [DeployApi.file], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun deleteFile(@RequestBody @Valid filePo: FilePo): ResponseEntity<InfoVo> {
        return deployFileDomain.deleteFile(filePo.path ?: "", filePo.name!!).let {
            ResponseEntity.ok(InfoVo(message = "文件删除成功"))
        }
    }

    @ApiOperation(value = "下载文件")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @GetMapping(value = [DeployApi.fileDownLoad], produces = [MediaType.ALL_VALUE])
    @Throws(ServerException::class)
    fun downloadFile(
        request: HttpServletRequest, response: HttpServletResponse,
        @ApiParam(value = "文件内容", required = true)
        @NotBlank(message = "文件内容不能为空")
        @RequestParam params: String
    ) {
        val fileContent = objectMapper.readValue(params, FilePo::class.java)
        if (CommonTools.isNullStr(fileContent.name)) {
            throw ServerException("文件名称不能为空")
        }
        val basePath = String(Base64.decode(fileContent.path), Charset.forName(CommonTools.getDefaultCharset()))
        val fileName = String(Base64.decode(fileContent.name), Charset.forName(CommonTools.getDefaultCharset()))
        deployFileDomain.doDownLoadFile(request, response, basePath, fileName)
    }
}