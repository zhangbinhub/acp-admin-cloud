package pers.acp.springcloud.common.log;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangbin by 11/07/2018 13:34
 * @since JDK 11
 */
@Component
@Scope("prototype")
public class LogInfo {

    public String getServerIp() {
        return serverIp;
    }

    public String getServerName() {
        return serverName;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public Integer getLogLevel() {
        return logLevel;
    }

    void setLogLevel(Integer logLevel) {
        this.logLevel = logLevel;
    }

    public Long getServerTime() {
        return serverTime;
    }

    public void setServerTime(Long serverTime) {
        this.serverTime = serverTime;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLineno() {
        return lineno;
    }

    public void setLineno(int lineno) {
        this.lineno = lineno;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public List<Object> getParams() {
        return params;
    }

    void setParams(List<Object> params) {
        this.params = params;
    }

    static {
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            serverIp = "";
        }
    }

    private static String serverIp;

    @Value("${spring.application.name}")
    private String serverName;

    /**
     * 日志类型字符串
     * 在 log-server 的 logback.xml 中对应配置日志规则，可实现不同类型的日志记录到的文件
     *
     * @see LogConstant#DEFAULT_TYPE
     * 默认日志类型为“ALL”，新增日志类型之后需在 log-server 中的 logback.xml 参照 ALL 进行配置
     */
    private String logType = LogConstant.DEFAULT_TYPE;

    private Integer logLevel;

    private Long serverTime;

    private String className;

    private int lineno;

    private String message;

    private Throwable throwable = null;

    private List<Object> params = new ArrayList<>();

}
