package pers.acp.admin.route.consumer

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel
import pers.acp.admin.route.constant.GateWayConstant

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
interface RouteLogInput {

    @Input(GateWayConstant.ROUTE_LOG_INPUT)
    fun input(): SubscribableChannel

}
