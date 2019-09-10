package pers.acp.admin.log.consumer

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.SubscribableChannel
import pers.acp.admin.log.constant.LogServerConstant

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
interface RouteLogInput {

    @Input(LogServerConstant.ROUTE_LOG_INPUT)
    fun input(): SubscribableChannel

}
