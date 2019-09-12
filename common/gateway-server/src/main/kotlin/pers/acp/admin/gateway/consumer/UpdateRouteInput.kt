package pers.acp.admin.gateway.consumer

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel
import pers.acp.admin.constant.RouteConstant

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
interface UpdateRouteInput {

    @Input(RouteConstant.UPDATE_ROUTE_INPUT)
    fun input(): SubscribableChannel

}
