package pers.acp.admin.log.domain

import org.bouncycastle.util.encoders.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.log.conf.LogServerCustomerConfiguration
import pers.acp.admin.log.constant.LogBackUp
import pers.acp.core.CommonTools
import pers.acp.core.task.timer.Calculation
import pers.acp.spring.boot.component.FileDownLoadHandle
import pers.acp.spring.boot.exceptions.ServerException

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.File
import java.nio.charset.Charset
import java.util.Comparator

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class LogFileDomain @Autowired
constructor(private val logServerCustomerConfiguration: LogServerCustomerConfiguration, private val fileDownLoadHandle: FileDownLoadHandle) {

    @Throws(ServerException::class)
    private fun validateFold(fold: File) {
        if (!fold.exists()) {
            if (!fold.mkdirs()) {
                throw ServerException("备份路径不存在")
            }
        }
        if (!fold.isDirectory) {
            throw ServerException("路径 " + fold.canonicalPath + " 不是文件夹")
        }
    }

    @Throws(ServerException::class)
    fun fileList(startDate: String, endDate: String): List<String> {
        val fold = File(logServerCustomerConfiguration.logFilePath + LogBackUp.BACK_UP_PATH)
        validateFold(fold)
        val fileList: MutableList<String> = mutableListOf()
        fold.listFiles { file ->
            val prefix = file.name.substring(0, LogBackUp.ZIP_FILE_PREFIX.length + Calculation.DATE_FORMAT.length)
            prefix >= LogBackUp.ZIP_FILE_PREFIX + startDate && prefix <= LogBackUp.ZIP_FILE_PREFIX + endDate
        }?.let {
            for (file in it) {
                fileList.add(file.name)
            }
        }
        fileList.sortWith(Comparator.reverseOrder())
        return fileList
    }

    @Throws(ServerException::class)
    fun doDownLoadFile(request: HttpServletRequest, response: HttpServletResponse, fileName: String) {
        val fold = File(logServerCustomerConfiguration.logFilePath + LogBackUp.BACK_UP_PATH)
        val foldPath = fold.canonicalPath
        validateFold(fold)
        val targetFileName = String(Base64.decode(fileName), Charset.forName(CommonTools.getDefaultCharset()))
        val filePath = "$foldPath/$targetFileName".replace("/", File.separator).replace("\\", File.separator)
        if (!File(filePath).exists()) {
            throw ServerException("文件[$fileName]不存在")
        }
        fileDownLoadHandle.downLoadFile(request, response, filePath, listOf("$foldPath/.*"))
    }

}
