package pers.acp.admin.oauth.producer.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import pers.acp.admin.oauth.constant.UpdateBindChannelConstant;
import pers.acp.admin.oauth.producer.UpdateRouteOutput;

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
