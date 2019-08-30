package pers.acp.admin.gateway.producer

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import pers.acp.admin.gateway.constant.GateWayConstant

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
interface RouteLogOutput {

    @Output(GateWayConstant.ROUTE_LOG_OUTPUT)
    fun sendMessage(): MessageChannel

}
