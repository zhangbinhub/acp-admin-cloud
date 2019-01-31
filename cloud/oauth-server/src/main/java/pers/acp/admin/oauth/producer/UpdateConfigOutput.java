package pers.acp.admin.oauth.producer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import pers.acp.admin.oauth.constant.UpdateConfigConstant;

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
public interface UpdateConfigOutput {

    @Output(UpdateConfigConstant.OUTPUT)
    MessageChannel sendMessage();

}
