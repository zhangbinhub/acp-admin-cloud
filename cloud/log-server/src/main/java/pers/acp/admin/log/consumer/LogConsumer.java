package pers.acp.admin.log.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.exceptions.EnumValueUndefinedException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.task.threadpool.ThreadPoolService;
import pers.acp.core.task.threadpool.basetask.BaseThreadTask;
import pers.acp.springcloud.common.enums.LogLevel;
import pers.acp.springcloud.common.log.LogConstant;
import pers.acp.springcloud.common.log.LogInfo;
import pers.acp.springcloud.common.log.LogInput;
import pers.acp.springcloud.common.log.LogInstance;

/**
 * @author zhangbin by 11/07/2018 18:50
 * @since JDK 11
 */
@Component
@EnableBinding(LogInput.class)
public class LogConsumer {

    private final LogInstance logInstance;

    private final ObjectMapper objectMapper;

    @Autowired
    public LogConsumer(LogInstance logInstance, ObjectMapper objectMapper) {
        this.logInstance = logInstance;
        this.objectMapper = objectMapper;
    }

    @StreamListener(LogConstant.INPUT)
    public void process(String message) {
        try {
            LogInfo logInfo = objectMapper.readValue(message, LogInfo.class);
            String logType = LogConstant.DEFAULT_TYPE;
            if (!CommonTools.isNullStr(logInfo.getLogType())) {
                logType = logInfo.getLogType();
            }
            logInfo.setLogType(logType);
            // 每个日志类型启动一个线程池，池中仅有一个线程，保证每个类型的消息顺序处理
            ThreadPoolService threadPoolService = ThreadPoolService.getInstance(logType + "_log", 3000, 1);
            threadPoolService.addTask(new BaseThreadTask(logType + "_log") {
                @Override
                public boolean beforeExcuteFun() {
                    return true;
                }

                @Override
                public Object excuteFun() {
                    doLog(logInfo);
                    return true;
                }

                @Override
                public void afterExcuteFun(Object result) {

                }
            });
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
    }

    private void doLog(LogInfo logInfo) {
        LogFactory logFactory = LogFactory.getInstance(logInfo.getLogType());
        StringBuilder message = new StringBuilder();
        LogLevel logLevel;
        try {
            logLevel = LogLevel.getEnum(logInfo.getLogLevel());
        } catch (EnumValueUndefinedException e) {
            logFactory.error(e.getMessage(), e);
            logLevel = LogLevel.OTHER;
        }
        message.append("[ ").append(CommonTools.strFillIn(logLevel.getName(), 5, 1, " ")).append(" ] ")
                .append("[").append(logInfo.getServerName()).append("] ")
                .append("[").append(logInfo.getServerIp()).append("] ")
                .append("[").append(logInfo.getServerPort()).append("] ")
                .append("[").append(logInfo.getClassName()).append("] ")
                .append("[ ").append(logInfo.getLineno()).append(" ] - ")
                .append(logInfo.getMessage());
        Throwable throwable = logInfo.getThrowable();
        switch (logLevel) {
            case DEBUG:
                logFactory.debug(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.debug(message.toString(), logInfo.getParams().toArray());
                }
                break;
            case WARN:
                logFactory.warn(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.warn(message.toString(), logInfo.getParams().toArray());
                }
                break;
            case ERROR:
                logFactory.error(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.error(message.toString(), logInfo.getParams().toArray());
                }
                break;
            case TRACE:
                logFactory.trace(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.trace(message.toString(), logInfo.getParams().toArray());
                }
                break;
            default:
                logFactory.info(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    logFactory.info(message.toString(), logInfo.getParams().toArray());
                }
                break;
        }
    }

}
