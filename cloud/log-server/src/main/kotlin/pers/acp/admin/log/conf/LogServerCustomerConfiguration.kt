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

    @Value("\${server.address}")
    val serverIp: String? = null

    @Value("\${server.port}")
    val serverPort: Int = 0

    @Value("\${logging.file}")
    val logFile: String = ""

    /**
     * 日志路径
     */
    @Value("\${logging.path}")
    val logFilePath: String = ""

    /**
     * 日志最大保留天数，默认 180 天
     */
    @Value("\${log-server.max-history-day-number}")
    val maxHistoryDayNumber: Int = 180

    /**
     * 是否记录路由日志
     */
    @Value("\${log-server.route-log.enabled}")
    val routeLogEnabled: Boolean = true

    /**
     * 是否记录操作日志
     */
    @Value("\${log-server.operate-log.enabled}")
    val operateLogEnabled: Boolean = true

    /**
     * 日志迁移时，单个事务处理的日志记录数量
     */
    @Value("\${log-server.quantity-per-process}")
    val quantityPerProcess: Int = 100

}
