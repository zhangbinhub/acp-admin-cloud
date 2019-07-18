package pers.acp.admin.gateway.producer.instance

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.MessageBuilder
import pers.acp.admin.gateway.po.RouteLogPO
import pers.acp.admin.gateway.producer.RouteLogOutput

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class RouteLogProducer @Autowired
constructor(private val routeLogOutput: RouteLogOutput, private val objectMapper: ObjectMapper) {

    @Throws(JsonProcessingException::class)
    fun doNotifyRouteLog(routeLogPO: RouteLogPO) {
        routeLogOutput.sendMessage().send(MessageBuilder.withPayload(objectMapper.writeValueAsString(routeLogPO)).build())
    }

}
