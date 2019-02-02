package pers.acp.admin.log.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.admin.common.constant.RuntimeName;
import pers.acp.admin.common.vo.RuntimeConfigVO;
import pers.acp.admin.log.conf.LogServerCustomerConfiguration;
import pers.acp.admin.log.constant.LogBackUp;
import pers.acp.admin.log.feign.Oauth;
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

    private final LogInstance logInstance;

    private final LogServerCustomerConfiguration logServerCustomerConfiguration;

    private final Oauth oauth;

    @Autowired
    public LogFileBackUpTask(LogInstance logInstance, LogServerCustomerConfiguration logServerCustomerConfiguration, Oauth oauth) {
        this.logInstance = logInstance;
        this.logServerCustomerConfiguration = logServerCustomerConfiguration;
        this.oauth = oauth;
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
            Calendar prevCalandar = CalendarTools.getPrevDay(CalendarTools.getCalendar());
            String logFileDate = CommonTools.getDateTimeString(prevCalandar.getTime(), LogBackUp.DATE_FORMAT);
            File fold = new File(logServerCustomerConfiguration.getLogFilePath());
            String logFileFold = fold.getAbsolutePath();
            if (!fold.exists() || !fold.isDirectory()) {
                throw new ServerException("路径 " + logFileFold + " 不存在或不是文件夹");
            }
            File[] files = fold.listFiles(pathname -> pathname.getName().contains(logFileDate));
            if (files != null && files.length > 0) {
                logInstance.info(logFileFold + " 路径下 " + logFileDate + " 的日志文件（或文件夹）共 " + files.length + " 个");
                String zipFilePath = logFileFold + LogBackUp.BACK_UP_PATH + File.separator + LogBackUp.ZIP_FILE_PREFIX + logFileDate + LogBackUp.EXTENSION;
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
                logInstance.info(logFileFold + " 路径下没有 " + logFileDate + " 的日志文件");
            }
            doClearBackUpFiles();
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public void afterExcuteFun(Object o) {

    }

    private void doClearBackUpFiles() throws TimerException {
        RuntimeConfigVO runtimeConfigVO = oauth.findRuntimeByName(RuntimeName.logServerBackUpMaxHistory);
        int maxHistory = Integer.valueOf(runtimeConfigVO.getValue());
        logInstance.info("开始清理历史备份文件，最大保留天数：" + maxHistory);
        List<String> filterNames = new ArrayList<>();
        Calendar day = CalendarTools.getPrevDay(CalendarTools.getCalendar());
        for (int i = 0; i < maxHistory; i++) {
            filterNames.add(LogBackUp.ZIP_FILE_PREFIX + CommonTools.getDateTimeString(day.getTime(), LogBackUp.DATE_FORMAT) + LogBackUp.EXTENSION);
            day = CalendarTools.getPrevDay(day);
        }
        File backUpFold = new File(logServerCustomerConfiguration.getLogFilePath() + LogBackUp.BACK_UP_PATH);
        if (backUpFold.exists()) {
            File[] files = backUpFold.listFiles(pathname -> !filterNames.contains(pathname.getName()));
            if (files != null) {
                for (File file : files) {
                    CommonTools.doDeleteFile(file, false);
                }
            }
        }
        logInstance.info("清理历史备份文件完成！");
    }

}
