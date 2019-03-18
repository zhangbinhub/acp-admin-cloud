package pers.acp.admin.oauth.producer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import pers.acp.admin.oauth.constant.UpdateBindChannelConstant;

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
public interface UpdateRouteOutput {

    @Output(UpdateBindChannelConstant.UPDATE_GATEWAY_OUTPUT)
    MessageChannel sendMessage();

}
