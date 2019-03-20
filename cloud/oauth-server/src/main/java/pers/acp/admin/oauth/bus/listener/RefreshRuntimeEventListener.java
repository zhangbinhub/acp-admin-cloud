package pers.acp.admin.oauth.bus.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pers.acp.admin.oauth.bus.event.RefreshRuntimeEvent;
import pers.acp.admin.oauth.domain.RuntimeConfigDomain;
import pers.acp.springcloud.common.log.LogInstance;

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
@Component
public class RefreshRuntimeEventListener implements ApplicationListener<RefreshRuntimeEvent> {

    private final LogInstance logInstance;

    private final ObjectMapper objectMapper;

    private final RuntimeConfigDomain runtimeConfigDomain;

    @Autowired
    public RefreshRuntimeEventListener(LogInstance logInstance, ObjectMapper objectMapper, RuntimeConfigDomain runtimeConfigDomain) {
        this.logInstance = logInstance;
        this.objectMapper = objectMapper;
        this.runtimeConfigDomain = runtimeConfigDomain;
    }

    @Override
    public void onApplicationEvent(RefreshRuntimeEvent refreshRuntimeEvent) {
        logInstance.info("收到更新应用信息事件：" + refreshRuntimeEvent.getMessage());
        try {
            logInstance.debug(objectMapper.writeValueAsString(refreshRuntimeEvent));
            logInstance.info("开始刷新运行参数数据...");
            runtimeConfigDomain.loadRuntimeConfig();
            logInstance.info("运行参数数据刷新完成！");
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
    }
}
