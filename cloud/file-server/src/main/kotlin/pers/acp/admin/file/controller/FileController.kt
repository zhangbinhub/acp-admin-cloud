package pers.acp.admin.file.controller

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pers.acp.admin.common.vo.InfoVO
import pers.acp.admin.file.constant.FileApi
import pers.acp.admin.file.constant.FileParams
import pers.acp.admin.file.domain.FileDownLoadDomain
import pers.acp.admin.file.po.FileDownLoadPO
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.vo.ErrorVO
import pers.acp.spring.cloud.log.LogInstance

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid
import java.io.File

/**
 * @author zhang by 16/04/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(FileApi.basePath)
@Api("文件信息")
class FileController @Autowired
constructor(private val logInstance: LogInstance, private val fileDownLoadDomain: FileDownLoadDomain) {

    private fun formatPath(filePath: String): String {
        var uploadPath = filePath
        if (CommonTools.isNullStr(uploadPath)) {
            uploadPath = FileParams.upLoadPath
        }
        var path = uploadPath.replace("\\", File.separator).replace("/", File.separator)
        if (path.startsWith(File.separator)) {
            path = path.substring(File.separator.length)
        }
        if (path.lastIndexOf(File.separator) < path.length - File.separator.length) {
            path += File.separator
        }
        return path
    }

    @ApiOperation(value = "文件上传")
    @ApiResponses(ApiResponse(code = 400, message = "上传失败", response = ErrorVO::class))
    @PostMapping(value = [FileApi.upLoad], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun upLoad(@RequestParam("file") file: MultipartFile,
               @ApiParam(value = "文件路径")
               @RequestParam(required = false)
               filePath: String,
               @ApiParam(value = "上传文件名")
               @RequestParam(required = false)
               destFileName: String): ResponseEntity<InfoVO> {
        var fileName = destFileName
        val path = formatPath(filePath)
        val originFileName = file.originalFilename
        if (CommonTools.isNullStr(fileName)) {
            fileName = System.currentTimeMillis().toString() + "_" + originFileName
        }
        if (file.isEmpty) {
            throw ServerException("请选择上传文件")
        }
        if (doUpLoad(file, path, fileName)) {
            return ResponseEntity.ok(InfoVO(message = path.replace(File.separator, "/") + fileName))
        } else {
            throw ServerException("文件上传失败【$originFileName】")
        }
    }

    @ApiOperation(value = "文件上传")
    @ApiResponses(ApiResponse(code = 400, message = "上传失败", response = ErrorVO::class))
    @PostMapping(value = [FileApi.upLoads], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun upLoad(@RequestParam("files") files: Array<MultipartFile>,
               @ApiParam(value = "文件路径")
               @RequestParam(required = false)
               filePath: String): ResponseEntity<InfoVO> {
        val path = formatPath(filePath)
        val resultPath = StringBuilder()
        val total = files.size
        for (i in 0 until total) {
            val file = files[i]
            val originFileName = file.originalFilename
            val destFileName = System.currentTimeMillis().toString() + "_" + originFileName
            if (file.isEmpty) {
                logInstance.error("文件为空【" + file.originalFilename + "】")
                break
            }
            if (doUpLoad(file, path, destFileName)) {
                val result = path.replace(File.separator, "/") + destFileName
                if (i == total - 1) {
                    resultPath.append(result)
                } else {
                    resultPath.append(result).append(",")
                }
            } else {
                logInstance.error("文件上传失败【$originFileName】")
                break
            }
        }
        return ResponseEntity.ok(InfoVO(message = resultPath.toString()))
    }

    private fun doUpLoad(file: MultipartFile, filePath: String, destFileName: String): Boolean =
            try {
                val destFile = File(filePath + destFileName)
                val fold = destFile.parentFile
                if (!fold.exists()) {
                    if (!fold.mkdirs()) {
                        throw ServerException("创建路径失败：" + fold.absolutePath)
                    }
                }
                file.transferTo(destFile.absoluteFile)
                true
            } catch (ex: Exception) {
                logInstance.error(ex.message, ex)
                false
            }

    @ApiOperation(value = "文件下载")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PostMapping(value = [FileApi.downLoad], produces = [MediaType.ALL_VALUE])
    @Throws(ServerException::class)
    fun download(request: HttpServletRequest, response: HttpServletResponse,
                 @RequestBody @Valid fileDownLoadPO: FileDownLoadPO) {
        if (CommonTools.isNullStr(fileDownLoadPO.filePath)) {
            throw ServerException("文件路径不能为空")
        }
        fileDownLoadDomain.doDownLoadFile(request, response, fileDownLoadPO.filePath!!)
    }

}
