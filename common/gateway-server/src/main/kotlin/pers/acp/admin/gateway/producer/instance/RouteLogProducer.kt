package pers.acp.admin.gateway.producer.instance

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.messaging.support.MessageBuilder
import pers.acp.admin.gateway.message.RouteLogMessage
import pers.acp.admin.gateway.producer.RouteLogOutput

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class RouteLogProducer(private val routeLogOutput: RouteLogOutput, private val objectMapper: ObjectMapper) {

    @Throws(JsonProcessingException::class)
    fun doNotifyRouteLog(routeLogMessage: RouteLogMessage) {
        GlobalScope.launch(Dispatchers.IO) {
            routeLogOutput.sendMessage().send(MessageBuilder.withPayload(objectMapper.writeValueAsString(routeLogMessage)).build())
        }
    }

}
