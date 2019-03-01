package pers.acp.admin.log.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component
@RefreshScope
public class LogServerCustomerConfiguration {

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public int getMaxHistoryDayNumber() {
        return maxHistoryDayNumber;
    }

    public void setMaxHistoryDayNumber(int maxHistoryDayNumber) {
        this.maxHistoryDayNumber = maxHistoryDayNumber;
    }

    /**
     * 日志路径
     */
    @Value("${logging.path}")
    private String logFilePath;

    /**
     * 日志最大保留天数，默认 180 天
     */
    @Value("${acp-admin.log-server.max-history-day-number}")
    private int maxHistoryDayNumber = 180;

}
