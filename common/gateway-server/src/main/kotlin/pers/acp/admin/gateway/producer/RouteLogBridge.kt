package pers.acp.admin.gateway.producer

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.MessageBuilder
import pers.acp.admin.gateway.message.RouteLogMessage

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class RouteLogBridge(
    private val streamBridge: StreamBridge,
    private val objectMapper: ObjectMapper,
    private val bindName: String
) {
    fun sendMessage(routeLogMessage: RouteLogMessage) {
        GlobalScope.launch(Dispatchers.IO) {
            streamBridge.send(
                bindName,
                MessageBuilder.withPayload(objectMapper.writeValueAsString(routeLogMessage)).build()
            )
        }
    }
}
