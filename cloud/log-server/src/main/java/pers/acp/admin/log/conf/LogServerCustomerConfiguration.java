package pers.acp.admin.log.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component
public class LogServerCustomerConfiguration {

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Value("${logging.path}")
    private String logFilePath;

}
