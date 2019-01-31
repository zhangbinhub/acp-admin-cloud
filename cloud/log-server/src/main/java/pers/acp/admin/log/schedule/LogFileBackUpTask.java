package pers.acp.admin.log.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.acp.admin.log.conf.LogServerCustomerConfiguration;
import pers.acp.core.CalendarTools;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.file.FileOperation;
import pers.acp.springboot.core.base.BaseSpringBootScheduledTask;
import pers.acp.springboot.core.exceptions.ServerException;

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

    public static String BACK_UP_PATH = File.separator + "backup";

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final LogServerCustomerConfiguration logServerCustomerConfiguration;

    @Autowired
    public LogFileBackUpTask(LogServerCustomerConfiguration logServerCustomerConfiguration) {
        this.logServerCustomerConfiguration = logServerCustomerConfiguration;
        setTaskName("日志文件备份任务");
    }

    @Override
    public boolean beforeExcuteFun() {
        log.info("开始执行日志文件备份");
        return true;
    }

    @Override
    public Object excuteFun() {
        try {
            Calendar prevCalandar = CalendarTools.getPrevDay(CalendarTools.getCalendar());
            String logFileDate = CommonTools.getDateTimeString(prevCalandar.getTime(), "yyyy-MM-dd");
            File fold = new File(logServerCustomerConfiguration.getLogFilePath());
            String logFileFold = fold.getAbsolutePath();
            if (!fold.exists() || !fold.isDirectory()) {
                throw new ServerException("路径 " + logFileFold + " 不存在或不是文件夹");
            }
            File[] files = fold.listFiles(pathname -> pathname.getName().contains(logFileDate));
            if (files != null && files.length > 0) {
                log.info(logFileFold + " 路径下 " + logFileDate + " 的日志文件（或文件夹）共 " + files.length + " 个");
                String zipFilePath = fold.getAbsolutePath() + BACK_UP_PATH + File.separator + "log_" + logFileDate + ".zip";
                List<String> fileNames = new ArrayList<>();
                for (File file : files) {
                    fileNames.add(file.getAbsolutePath());
                }
                log.info("开始执行文件压缩...");
                zipFilePath = FileOperation.filesToZIP(fileNames.toArray(new String[]{}), zipFilePath, true);
                if (!CommonTools.isNullStr(zipFilePath)) {
                    log.info("文件压缩完成，压缩文件为：" + zipFilePath);
                } else {
                    log.info("文件压缩失败！");
                }
            } else {
                log.info(logFileFold + " 路径下没有 " + logFileDate + " 的日志文件");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public void afterExcuteFun(Object o) {

    }

}
