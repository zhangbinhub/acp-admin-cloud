package pers.acp.springcloud.log.consumer;

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

/**
 * @author zhangbin by 11/07/2018 18:50
 * @since JDK 11
 */
@Component
@EnableBinding(LogInput.class)
public class LogConsumer {

    @StreamListener(LogConstant.INPUT)
    public void process(String message) {
        LogInfo logInfo = CommonTools.jsonToObject(CommonTools.getJsonFromStr(message), LogInfo.class);
        String logType = LogConstant.DEFAULT_TYPE;
        if (!CommonTools.isNullStr(logInfo.getLogType())) {
            logType = logInfo.getLogType();
        }
        logInfo.setLogType(logType);
        // 每个日志类型启动一个线程池，池中仅有一个线程，保证每个类型的消息顺序处理
        ThreadPoolService threadPoolService = ThreadPoolService.getInstance(logType, 3000, 1);
        threadPoolService.addTask(new BaseThreadTask("log") {
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
    }

    private void doLog(LogInfo logInfo) {
        LogFactory log = LogFactory.getInstance(logInfo.getLogType());
        StringBuilder message = new StringBuilder();
        LogLevel logLevel;
        try {
            logLevel = LogLevel.getEnum(logInfo.getLogLevel());
        } catch (EnumValueUndefinedException e) {
            log.error(e.getMessage(), e);
            logLevel = LogLevel.OTHER;
        }
        message.append("[ ").append(CommonTools.strFillIn(logLevel.getName(), 5, 1, " ")).append(" ] ")
                .append("[").append(logInfo.getServerName()).append("] ")
                .append("[").append(logInfo.getServerIp()).append("] ")
                .append("[").append(logInfo.getClassName()).append("] ")
                .append("[ ").append(logInfo.getLineno()).append(" ] - ")
                .append(logInfo.getMessage());
        Throwable throwable = logInfo.getThrowable();
        switch (logLevel) {
            case DEBUG:
                log.debug(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    log.debug(message.toString(), logInfo.getParams().toArray());
                }
                break;
            case WARN:
                log.warn(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    log.warn(message.toString(), logInfo.getParams().toArray());
                }
                break;
            case ERROR:
                log.error(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    log.error(message.toString(), logInfo.getParams().toArray());
                }
                break;
            case TRACE:
                log.trace(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    log.trace(message.toString(), logInfo.getParams().toArray());
                }
                break;
            default:
                log.info(message.toString(), throwable);
                if (!logInfo.getParams().isEmpty()) {
                    log.info(message.toString(), logInfo.getParams().toArray());
                }
                break;
        }
    }

}
