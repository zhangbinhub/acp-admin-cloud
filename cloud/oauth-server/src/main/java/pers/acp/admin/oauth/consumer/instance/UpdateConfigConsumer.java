package pers.acp.admin.oauth.consumer.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import pers.acp.admin.oauth.constant.UpdateConfigConstant;
import pers.acp.admin.oauth.consumer.UpdateConfigInput;
import pers.acp.admin.oauth.domain.RuntimeConfigDomain;
import pers.acp.admin.oauth.domain.security.SecurityClientDetailsService;
import pers.acp.springcloud.common.log.LogInstance;

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@Component
@EnableBinding(UpdateConfigInput.class)
public class UpdateConfigConsumer {

    private final LogInstance logInstance;

    private final SecurityClientDetailsService securityClientDetailsService;

    private final RuntimeConfigDomain runtimeConfigDomain;

    @Autowired
    public UpdateConfigConsumer(LogInstance logInstance, SecurityClientDetailsService securityClientDetailsService, RuntimeConfigDomain runtimeConfigDomain) {
        this.logInstance = logInstance;
        this.securityClientDetailsService = securityClientDetailsService;
        this.runtimeConfigDomain = runtimeConfigDomain;
    }

    @StreamListener(UpdateConfigConstant.INPUT)
    public void process(String message) {
        logInstance.info("收到 kafka 消息：" + message);
        try {
            switch (message) {
                case UpdateConfigConstant.UPDATE_APP:
                    logInstance.info("开始刷新client数据...");
                    securityClientDetailsService.loadClientInfo();
                    logInstance.info("client数据刷新完成！");
                    break;
                case UpdateConfigConstant.UPDATE_RUNTIME:
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
