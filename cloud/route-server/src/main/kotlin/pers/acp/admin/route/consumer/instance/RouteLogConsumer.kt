package pers.acp.admin.route.consumer.instance

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.StreamListener
import pers.acp.admin.route.constant.GateWayConstant
import pers.acp.admin.route.domain.RouteLogDomain
import pers.acp.admin.route.entity.RouteLog

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class RouteLogConsumer @Autowired
constructor(private val objectMapper: ObjectMapper, private val routeLogDomain: RouteLogDomain) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @StreamListener(GateWayConstant.ROUTE_LOG_INPUT)
    fun process(message: String) {
        log.debug("收到 kafka 消息：$message")
        try {
            routeLogDomain.doLog(objectMapper.readValue(message, RouteLog::class.java))
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

}
