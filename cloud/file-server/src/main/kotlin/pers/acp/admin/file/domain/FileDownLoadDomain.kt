package pers.acp.admin.file.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.spring.boot.component.FileDownLoadHandle
import pers.acp.spring.boot.exceptions.ServerException

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.File

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class FileDownLoadDomain @Autowired
constructor(private val fileDownLoadHandle: FileDownLoadHandle) {

    @Throws(ServerException::class)
    fun doDownLoadFile(request: HttpServletRequest, response: HttpServletResponse, filePath: String) {
        val filePathFormat = filePath.replace("/", File.separator).replace("\\", File.separator)
        if (!File(filePathFormat).exists()) {
            throw ServerException("文件下载失败，文件[$filePath]不存在")
        }
        fileDownLoadHandle.downLoadFile(request, response, filePath, false, listOf(filePath))
    }

}
