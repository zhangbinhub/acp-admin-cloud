package pers.acp.admin.log.conf

import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component
@RefreshScope
class LogServerCustomerConfiguration {

    /**
     * 日志路径
     */
    @Value("\${logging.path}")
    var logFilePath: String = ""

    /**
     * 日志最大保留天数，默认 180 天
     */
    @Value("\${log-server.max-history-day-number}")
    var maxHistoryDayNumber = 180

}
