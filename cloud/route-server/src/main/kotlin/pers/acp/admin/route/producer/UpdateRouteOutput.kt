package pers.acp.admin.route.producer

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import pers.acp.admin.constant.RouteConstant

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
interface UpdateRouteOutput {

    @Output(RouteConstant.UPDATE_GATEWAY_OUTPUT)
    fun sendMessage(): MessageChannel

}
