package pers.acp.admin.route.producer

import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.MessageBuilder
import pers.acp.admin.constant.RouteConstant

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class UpdateRouteBridge(
    private val streamBridge: StreamBridge,
    private val bindName: String
) {
    fun doNotifyUpdateRoute() {
        streamBridge.send(
            bindName,
            MessageBuilder.withPayload(RouteConstant.UPDATE_GATEWAY_ROUTES).build()
        )
    }
}
