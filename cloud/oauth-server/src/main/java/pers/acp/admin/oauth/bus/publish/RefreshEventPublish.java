package pers.acp.admin.oauth.bus.publish;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pers.acp.admin.oauth.bus.event.RefreshApplicationEvent;
import pers.acp.admin.oauth.bus.event.RefreshRuntimeEvent;

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
@Component
public class RefreshEventPublish {

    private final ApplicationContext applicationContext;

    private final BusProperties busProperties;

    @Autowired
    public RefreshEventPublish(ApplicationContext applicationContext, BusProperties busProperties) {
        this.applicationContext = applicationContext;
        this.busProperties = busProperties;
    }

    public void doNotifyUpdateApp() {
        RefreshApplicationEvent applicationEvent = new RefreshApplicationEvent(this, busProperties.getId(), null, "refresh client");
        applicationContext.publishEvent(applicationEvent);
    }

    public void doNotifyUpdateRuntime() {
        RefreshRuntimeEvent runtimeEvent = new RefreshRuntimeEvent(this, busProperties.getId(), null, "refresh runtime");
        applicationContext.publishEvent(runtimeEvent);
    }

}
