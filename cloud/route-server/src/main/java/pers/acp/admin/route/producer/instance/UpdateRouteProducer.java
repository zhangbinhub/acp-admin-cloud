package pers.acp.admin.route.producer.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import pers.acp.admin.route.constant.UpdateBindChannelConstant;
import pers.acp.admin.route.producer.UpdateRouteOutput;

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
public class UpdateRouteProducer {

    private final UpdateRouteOutput updateRouteOutput;

    @Autowired
    public UpdateRouteProducer(UpdateRouteOutput updateRouteOutput) {
        this.updateRouteOutput = updateRouteOutput;
    }

    public void doNotifyUpdateRoute() {
        updateRouteOutput.sendMessage().send(MessageBuilder.withPayload(UpdateBindChannelConstant.UPDATE_GATEWAY_ROUTES).build());
    }

}
