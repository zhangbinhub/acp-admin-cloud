package pers.acp.admin.deploy.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.deploy.conf.DeployServerCustomerConfiguration
import io.github.zhangbinhub.acp.core.CommonTools
import pers.acp.admin.deploy.vo.FileVo
import io.github.zhangbinhub.acp.boot.component.FileDownLoadHandle
import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
@Transactional(readOnly = true)
class DeployFileDomain @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val deployServerCustomerConfiguration: DeployServerCustomerConfiguration,
    private val fileDownLoadHandle: FileDownLoadHandle
) : BaseDomain() {
    @Throws(ServerException::class)
    private fun setupPermission(file: File) {
        System.getProperty("os.name", "").lowercase().apply {
            if (!this.startsWith("win")) {
                Runtime.getRuntime().exec("chmod -R 775 ${file.canonicalPath}").waitFor()
            }
        }
    }

    @Throws(ServerException::class)
    private fun makeFold(foldPath: String = ""): File =
        File(
            deployServerCustomerConfiguration.uploadPath + if (!CommonTools.isNullStr(foldPath)) {
                File.separator + foldPath.replace("\\", "/")
                    .replace(Regex("^[\\\\.|/]*"), "")
                    .replace("/", File.separator)
            } else {
                ""
            }
        ).apply {
            if (!this.exists()) {
                if (!this.mkdirs()) {
                    logAdapter.error("创建目录失败: " + this.canonicalPath)
                    throw ServerException("创建目录失败！")
                }
            }
        }

    @Throws(ServerException::class)
    fun createFold(path: String, foldName: String): FileVo =
        if (!CommonTools.isNullStr(path)) {
            path + File.separator + foldName
        } else {
            foldName
        }.let { foldPath ->
            makeFold(foldPath)
        }.apply {
            setupPermission(this)
        }.let { fold ->
            FileVo(
                directory = fold.isDirectory,
                name = fold.name,
                size = fold.length(),
                lastModified = fold.lastModified()
            )
        }

    @Throws(ServerException::class)
    fun fileList(path: String, name: String): List<FileVo> = makeFold(path).let { fold ->
        mutableListOf<FileVo>().apply {
            if (CommonTools.isNullStr(name)) {
                fold.listFiles()
            } else {
                fold.listFiles { file ->
                    file.name.contains(name)
                }
            }?.let {
                for (file in it) {
                    this.add(
                        FileVo(
                            directory = file.isDirectory,
                            name = file.name,
                            size = file.length(),
                            lastModified = file.lastModified()
                        )
                    )
                }
            }
        }.let { list ->
            list.sortedWith(compareBy({ it.name }, { it.lastModified }))
        }
    }

    @Throws(ServerException::class)
    fun uploadFile(path: String, file: MultipartFile): FileVo {
        val fold = makeFold(path)
        var fileName = file.originalFilename!!
        val existCount = fold.listFiles { item ->
            item.name == fileName
        }?.size ?: 0
        if (existCount > 0) {
            fileName = "${
                fileName.substring(
                    0,
                    fileName.lastIndexOf(".")
                )
            }_${existCount}${fileName.substring(fileName.lastIndexOf("."))}"
        }
        val targetFile = File(fold.canonicalPath + File.separator + fileName)
        file.transferTo(targetFile)
        setupPermission(targetFile)
        return FileVo(
            directory = targetFile.isDirectory,
            name = targetFile.name,
            size = targetFile.length(),
            lastModified = targetFile.lastModified()
        )
    }

    @Throws(ServerException::class)
    fun deleteFile(path: String, fileName: String) {
        var targetFileName = fileName.replace("/", File.separator).replace("\\", File.separator)
        val index = targetFileName.lastIndexOf(File.separator)
        if (index > -1) {
            targetFileName = targetFileName.substring(index + 1)
        }
        CommonTools.doDeleteFile(File("${makeFold(path).canonicalPath}${File.separator}$targetFileName"))
    }

    @Throws(ServerException::class)
    fun doDownLoadFile(request: HttpServletRequest, response: HttpServletResponse, path: String, fileName: String) {
        val fold = makeFold(path)
        val foldPath = fold.canonicalPath
        var targetFileName = fileName
        val index = targetFileName.lastIndexOf(File.separator)
        if (index > -1) {
            targetFileName = targetFileName.substring(index + 1)
        }
        val filePath = "$foldPath/$targetFileName".replace("/", File.separator).replace("\\", File.separator)
        if (!File(filePath).exists()) {
            throw ServerException("文件[$targetFileName]不存在")
        }
        fileDownLoadHandle.downLoadFile(request, response, filePath, listOf("$foldPath/.*"))
    }
}