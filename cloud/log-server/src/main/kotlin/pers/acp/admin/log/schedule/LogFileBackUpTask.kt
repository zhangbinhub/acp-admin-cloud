package pers.acp.admin.log.schedule

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pers.acp.admin.log.conf.LogServerCustomerConfiguration
import pers.acp.admin.log.constant.LogBackUp
import pers.acp.core.CalendarTools
import pers.acp.core.CommonTools
import pers.acp.core.task.timer.Calculation
import pers.acp.spring.boot.base.BaseSpringBootScheduledAsyncTask
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

import java.io.File

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component("LogFileBackUpTask")
class LogFileBackUpTask @Autowired
constructor(private val logAdapter: LogAdapter, private val logServerCustomerConfiguration: LogServerCustomerConfiguration) : BaseSpringBootScheduledAsyncTask() {

    init {
        taskName = "日志文件备份任务"
    }

    override fun beforeExecuteFun(): Boolean {
        logAdapter.info("开始执行日志文件备份")
        return true
    }

    override fun executeFun(): Any? {
        try {
            var day = CalendarTools.getPrevDay(CommonTools.getNowDateTime())
            for (i in 0 until logServerCustomerConfiguration.maxHistoryDayNumber) {
                val logFileDate = CommonTools.getDateTimeString(day, Calculation.DATE_FORMAT)
                val logFold = File(logServerCustomerConfiguration.logFilePath)
                val logFoldPath = logFold.canonicalPath
                var zipFilePath = logFoldPath + LogBackUp.BACK_UP_PATH + File.separator + LogBackUp.ZIP_FILE_PREFIX + logFileDate + "_" + logServerCustomerConfiguration.serverIp + "_" + logServerCustomerConfiguration.serverPort + LogBackUp.EXTENSION
                val zipFile = File(zipFilePath)
                if (!zipFile.exists()) {
                    if (!logFold.exists() || !logFold.isDirectory) {
                        throw ServerException("路径 $logFoldPath 不存在或不是文件夹")
                    }
                    val files = logFold.listFiles { pathname -> pathname.name.contains(logFileDate) }
                    if (files != null && files.isNotEmpty()) {
                        logAdapter.info(logFoldPath + " 路径下 " + logFileDate + " 的日志文件（或文件夹）共 " + files.size + " 个")
                        val fileNames: MutableList<String> = mutableListOf()
                        for (file in files) {
                            fileNames.add(file.canonicalPath)
                        }
                        logAdapter.info("开始执行文件压缩...")
                        zipFilePath = CommonTools.filesToZip(fileNames, zipFilePath, true)
                        if (!CommonTools.isNullStr(zipFilePath)) {
                            logAdapter.info("文件压缩完成，压缩文件为：$zipFilePath")
                        } else {
                            logAdapter.info("文件压缩失败！")
                        }
                    } else {
                        logAdapter.debug("$logFoldPath 路径下没有 $logFileDate 的日志文件")
                    }
                }
                day = CalendarTools.getPrevDay(day)
            }
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }

        return true
    }

    override fun afterExecuteFun(result: Any) {
        try {
            doClearBackUpFiles()
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }

    }

    private fun doClearBackUpFiles() {
        logAdapter.info("开始清理历史备份文件，最大保留天数：" + logServerCustomerConfiguration.maxHistoryDayNumber)
        val filterLogFileNames: MutableSet<String> = mutableSetOf()
        filterLogFileNames.add(logServerCustomerConfiguration.logFile.substring(logServerCustomerConfiguration.logFilePath.length + 1))
        filterLogFileNames.add(LogBackUp.BACK_UP_PATH.substring(1))
        val filterLogZipFileNames: MutableSet<String> = mutableSetOf()
        // 保留当天和历史最大天数的文件
        var day = CommonTools.getNowDateTime()
        for (i in 0..logServerCustomerConfiguration.maxHistoryDayNumber) {
            filterLogFileNames.add(CommonTools.getDateTimeString(day, Calculation.DATE_FORMAT))
            filterLogZipFileNames.add(LogBackUp.ZIP_FILE_PREFIX + CommonTools.getDateTimeString(day, Calculation.DATE_FORMAT) + "_" + logServerCustomerConfiguration.serverIp + "_" + logServerCustomerConfiguration.serverPort + LogBackUp.EXTENSION)
            day = CalendarTools.getPrevDay(day)
        }
        // 清理历史日志文件
        val fold = File(logServerCustomerConfiguration.logFilePath)
        doDeleteFileForFold(fold, filterLogFileNames)
        // 清理历史备份压缩日志文件
        val backUpFold = File(logServerCustomerConfiguration.logFilePath + LogBackUp.BACK_UP_PATH)
        doDeleteFileForFold(backUpFold, filterLogZipFileNames, true)
        logAdapter.info("清理历史备份文件完成！")
    }

    private fun doDeleteFileForFold(fold: File, filterNames: Set<String>, zipFile: Boolean = false) {
        if (fold.exists()) {
            if (zipFile) {
                fold.listFiles { file ->
                    if (file.name.contains(logServerCustomerConfiguration.serverIp + "_" + logServerCustomerConfiguration.serverPort + LogBackUp.EXTENSION, true)) {
                        !filterNames.contains(file.name)
                    } else {
                        false
                    }
                }
            } else {
                fold.listFiles { file -> !filterNames.contains(file.name) }
            }?.let {
                it.forEach { file ->
                    CommonTools.doDeleteFile(file, false)
                }
            }
        }
    }

}
