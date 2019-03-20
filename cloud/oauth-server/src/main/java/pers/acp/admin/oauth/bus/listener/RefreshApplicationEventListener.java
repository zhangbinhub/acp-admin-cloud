package pers.acp.admin.oauth.bus.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pers.acp.admin.oauth.bus.event.RefreshApplicationEvent;
import pers.acp.admin.oauth.domain.security.SecurityClientDetailsDomain;
import pers.acp.springcloud.common.log.LogInstance;

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
@Component
public class RefreshApplicationEventListener implements ApplicationListener<RefreshApplicationEvent> {

    private final LogInstance logInstance;

    private final ObjectMapper objectMapper;

    private final SecurityClientDetailsDomain securityClientDetailsDomain;

    @Autowired
    public RefreshApplicationEventListener(LogInstance logInstance, ObjectMapper objectMapper, SecurityClientDetailsDomain securityClientDetailsDomain) {
        this.logInstance = logInstance;
        this.objectMapper = objectMapper;
        this.securityClientDetailsDomain = securityClientDetailsDomain;
    }

    @Override
    public void onApplicationEvent(RefreshApplicationEvent refreshApplicationEvent) {
        logInstance.info("收到更新应用信息事件：" + refreshApplicationEvent.getMessage());
        try {
            logInstance.debug(objectMapper.writeValueAsString(refreshApplicationEvent));
            logInstance.info("开始刷新client数据...");
            securityClientDetailsDomain.loadClientInfo();
            logInstance.info("client数据刷新完成！");
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
    }

}
