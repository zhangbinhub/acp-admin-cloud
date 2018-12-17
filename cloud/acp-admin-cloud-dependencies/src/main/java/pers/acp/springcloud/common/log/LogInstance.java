package pers.acp.springcloud.common.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import pers.acp.core.CommonTools;
import pers.acp.core.log.LogFactory;
import pers.acp.springboot.core.tools.SpringBeanFactory;
import pers.acp.springcloud.common.enums.LogLevel;

import java.util.Arrays;
import java.util.Date;

/**
 * @author zhangbin by 11/07/2018 13:36
 * @since JDK 11
 */
@Component
@EnableBinding(LogOutput.class)
public class LogInstance {

    private static final LogFactory log = LogFactory.getInstance(LogInstance.class);

    private final LogOutput logOutput;

    @Autowired
    public LogInstance(LogOutput logOutput) {
        this.logOutput = logOutput;
    }

    private void sendToLogServer(LogInfo logInfo) {
        logInfo.setServerTime(new Date().getTime());
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        int lineno = 0;
        String className = "";
        if (stacks.length >= 4) {
            lineno = stacks[3].getLineNumber();
            className = stacks[3].getClassName();
        }
        logInfo.setLineno(lineno);
        logInfo.setClassName(className);
        logOutput.sendMessage().send(MessageBuilder.withPayload(CommonTools.objectToJson(logInfo).toString()).build());
    }

    public void info(String message) {
        log.info(message);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.INFO.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void info(String message, Object... var) {
        log.info(message, var);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.INFO.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void info(String message, Throwable t) {
        log.info(message, t);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.INFO.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

    public void debug(String message) {
        log.debug(message);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.DEBUG.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void debug(String message, Object... var) {
        log.debug(message, var);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.DEBUG.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void debug(String message, Throwable t) {
        log.debug(message, t);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.DEBUG.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

    public void warn(String message) {
        log.warn(message);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.WARN.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void warn(String message, Object... var) {
        log.warn(message, var);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.WARN.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void warn(String message, Throwable t) {
        log.warn(message, t);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.WARN.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

    public void error(String message) {
        log.error(message);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.ERROR.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void error(String message, Object... var) {
        log.error(message, var);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.ERROR.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void error(String message, Throwable t) {
        log.error(message, t);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.ERROR.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

    public void trace(String message) {
        log.trace(message);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.TRACE.getValue());
            logInfo.setMessage(message);
            sendToLogServer(logInfo);
        }
    }

    public void trace(String message, Object... var) {
        log.trace(message, var);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.TRACE.getValue());
            logInfo.setMessage(message);
            logInfo.setParams(Arrays.asList(var));
            sendToLogServer(logInfo);
        }
    }

    public void trace(String message, Throwable t) {
        log.trace(message, t);
        LogInfo logInfo = SpringBeanFactory.getBean(LogInfo.class);
        if (logInfo != null) {
            logInfo.setLogLevel(LogLevel.TRACE.getValue());
            logInfo.setMessage(message);
            logInfo.setThrowable(t);
            sendToLogServer(logInfo);
        }
    }

}
