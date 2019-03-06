package pers.acp.admin.oauth.consumer.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import pers.acp.admin.oauth.constant.UpdateBindChannelConstant;
import pers.acp.admin.oauth.consumer.UpdateConfigInput;
import pers.acp.admin.oauth.domain.RuntimeConfigDomain;
import pers.acp.admin.oauth.domain.security.SecurityClientDetailsDomain;
import pers.acp.springcloud.common.log.LogInstance;

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component
@EnableBinding(UpdateConfigInput.class)
public class UpdateConfigConsumer {

    private final LogInstance logInstance;

    private final SecurityClientDetailsDomain securityClientDetailsDomain;

    private final RuntimeConfigDomain runtimeConfigDomain;

    @Autowired
    public UpdateConfigConsumer(LogInstance logInstance, SecurityClientDetailsDomain securityClientDetailsDomain, RuntimeConfigDomain runtimeConfigDomain) {
        this.logInstance = logInstance;
        this.securityClientDetailsDomain = securityClientDetailsDomain;
        this.runtimeConfigDomain = runtimeConfigDomain;
    }

    @StreamListener(UpdateBindChannelConstant.INPUT)
    public void process(String message) {
        logInstance.info("收到 kafka 消息：" + message);
        try {
            switch (message) {
                case UpdateBindChannelConstant.UPDATE_APP:
                    logInstance.info("开始刷新client数据...");
                    securityClientDetailsDomain.loadClientInfo();
                    logInstance.info("client数据刷新完成！");
                    break;
                case UpdateBindChannelConstant.UPDATE_RUNTIME:
                    logInstance.info("开始刷新runtime数据...");
                    runtimeConfigDomain.loadRuntimeConfig();
                    logInstance.info("runtime数据刷新完成！");
                    break;
                default:
                    logInstance.info("无法识别更新操作：" + message);
            }
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
    }

}
