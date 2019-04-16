package pers.acp.admin.log.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pers.acp.admin.common.constant.CommonConstant;
import pers.acp.admin.log.conf.LogServerCustomerConfiguration;
import pers.acp.admin.log.constant.LogBackUp;
import pers.acp.core.CalendarTools;
import pers.acp.core.CommonTools;
import pers.acp.core.exceptions.TimerException;
import pers.acp.file.FileOperation;
import pers.acp.springboot.core.base.BaseSpringBootScheduledTask;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springcloud.common.log.LogInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component("LogFileBackUpTask")
public class LogFileBackUpTask extends BaseSpringBootScheduledTask {

    @Value("${server.address}")
    private String serverIp;

    @Value("${server.port}")
    private int serverPort;

    private final LogInstance logInstance;

    private LogServerCustomerConfiguration logServerCustomerConfiguration;

    @Autowired
    public LogFileBackUpTask(LogInstance logInstance, LogServerCustomerConfiguration logServerCustomerConfiguration) {
        this.logInstance = logInstance;
        this.logServerCustomerConfiguration = logServerCustomerConfiguration;
        setTaskName("日志文件备份任务");
    }

    @Override
    public boolean beforeExcuteFun() {
        logInstance.info("开始执行日志文件备份");
        return true;
    }

    @Override
    public Object excuteFun() {
        try {
            Calendar day = CalendarTools.getPrevDay(CalendarTools.getCalendar());
            for (int i = 0; i < logServerCustomerConfiguration.getMaxHistoryDayNumber(); i++) {
                String logFileDate = CommonTools.getDateTimeString(day.getTime(), CommonConstant.DATE_FORMAT);
                File logFold = new File(logServerCustomerConfiguration.getLogFilePath());
                String logFoldPath = logFold.getAbsolutePath();
                String zipFilePath = logFoldPath + LogBackUp.BACK_UP_PATH + File.separator + LogBackUp.ZIP_FILE_PREFIX + logFileDate + "_" + serverIp + "_" + serverPort + LogBackUp.EXTENSION;
                File zipFile = new File(zipFilePath);
                if (!zipFile.exists()) {
                    if (!logFold.exists() || !logFold.isDirectory()) {
                        throw new ServerException("路径 " + logFoldPath + " 不存在或不是文件夹");
                    }
                    File[] files = logFold.listFiles(pathname -> pathname.getName().contains(logFileDate));
                    if (files != null && files.length > 0) {
                        logInstance.info(logFoldPath + " 路径下 " + logFileDate + " 的日志文件（或文件夹）共 " + files.length + " 个");
                        List<String> fileNames = new ArrayList<>();
                        for (File file : files) {
                            fileNames.add(file.getAbsolutePath());
                        }
                        logInstance.info("开始执行文件压缩...");
                        zipFilePath = FileOperation.filesToZIP(fileNames.toArray(new String[]{}), zipFilePath, true);
                        if (!CommonTools.isNullStr(zipFilePath)) {
                            logInstance.info("文件压缩完成，压缩文件为：" + zipFilePath);
                        } else {
                            logInstance.info("文件压缩失败！");
                        }
                    } else {
                        logInstance.debug(logFoldPath + " 路径下没有 " + logFileDate + " 的日志文件");
                    }
                }
                day = CalendarTools.getPrevDay(day);
            }
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public void afterExcuteFun(Object o) {
        try {
            doClearBackUpFiles();
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
    }

    private void doClearBackUpFiles() throws TimerException {
        logInstance.info("开始清理历史备份文件，最大保留天数：" + logServerCustomerConfiguration.getMaxHistoryDayNumber());
        List<String> filterLogFileNames = new ArrayList<>();
        filterLogFileNames.add("spring.log");
        filterLogFileNames.add(LogBackUp.BACK_UP_PATH.substring(1));
        List<String> filterLogZipFileNames = new ArrayList<>();
        // 保留当天和历史最大天数的文件
        Calendar day = CalendarTools.getCalendar();
        for (int i = 0; i <= logServerCustomerConfiguration.getMaxHistoryDayNumber(); i++) {
            filterLogFileNames.add(CommonTools.getDateTimeString(day.getTime(), CommonConstant.DATE_FORMAT));
            filterLogZipFileNames.add(LogBackUp.ZIP_FILE_PREFIX + CommonTools.getDateTimeString(day.getTime(), CommonConstant.DATE_FORMAT) + "_" + serverIp + "_" + serverPort + LogBackUp.EXTENSION);
            day = CalendarTools.getPrevDay(day);
        }
        // 清理历史日志文件
        File fold = new File(logServerCustomerConfiguration.getLogFilePath());
        doDeleteFileForFold(fold, filterLogFileNames);
        // 清理历史备份压缩日志文件
        File backUpFold = new File(logServerCustomerConfiguration.getLogFilePath() + LogBackUp.BACK_UP_PATH);
        doDeleteFileForFold(backUpFold, filterLogZipFileNames);
        logInstance.info("清理历史备份文件完成！");
    }

    private void doDeleteFileForFold(File fold, List<String> filterNames) {
        if (fold.exists()) {
            File[] files = fold.listFiles(pathname -> !filterNames.contains(pathname.getName()));
            if (files != null) {
                for (File file : files) {
                    CommonTools.doDeleteFile(file, false);
                }
            }
        }
    }

}
