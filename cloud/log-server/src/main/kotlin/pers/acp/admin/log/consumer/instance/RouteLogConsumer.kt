package pers.acp.admin.log.consumer.instance

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.StreamListener
import pers.acp.admin.log.constant.LogServerConstant
import pers.acp.admin.log.domain.RouteLogDomain
import pers.acp.admin.log.entity.RouteLog

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class RouteLogConsumer @Autowired
constructor(private val objectMapper: ObjectMapper, private val routeLogDomain: RouteLogDomain) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @StreamListener(LogServerConstant.ROUTE_LOG_INPUT)
    fun process(message: String) {
        log.debug("收到 kafka 消息：$message")
        try {
            routeLogDomain.doLog(objectMapper.readValue(message, RouteLog::class.java))
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

}
