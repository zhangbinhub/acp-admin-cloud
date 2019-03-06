package pers.acp.admin.oauth.consumer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;
import pers.acp.admin.oauth.constant.UpdateBindChannelConstant;

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
public interface UpdateConfigInput {

    @Input(UpdateBindChannelConstant.INPUT)
    SubscribableChannel input();

}
