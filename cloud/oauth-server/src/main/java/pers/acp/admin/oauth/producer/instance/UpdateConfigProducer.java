package pers.acp.admin.oauth.producer.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import pers.acp.admin.oauth.constant.UpdateConfigConstant;
import pers.acp.admin.oauth.producer.UpdateConfigOutput;

/**
 * @author zhang by 31/01/2019
 * @since JDK 11
 */
@Component
@EnableBinding(UpdateConfigOutput.class)
public class UpdateConfigProducer {

    private final UpdateConfigOutput updateConfigOutput;

    @Autowired
    public UpdateConfigProducer(UpdateConfigOutput updateConfigOutput) {
        this.updateConfigOutput = updateConfigOutput;
    }

    public void doNotifyUpdateApp() {
        updateConfigOutput.sendMessage().send(MessageBuilder.withPayload(UpdateConfigConstant.UPDATE_APP).build());
    }

    public void doNotifyUpdateRuntime() {
        updateConfigOutput.sendMessage().send(MessageBuilder.withPayload(UpdateConfigConstant.UPDATE_RUNTIME).build());
    }

}
