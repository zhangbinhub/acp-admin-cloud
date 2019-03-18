package pers.acp.admin.gateway.consumer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;
import pers.acp.admin.gateway.constant.GateWayConstant;

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
public interface UpdateRouteInput {

    @Input(GateWayConstant.UPDATE_ROUTE_INPUT)
    SubscribableChannel input();

}
