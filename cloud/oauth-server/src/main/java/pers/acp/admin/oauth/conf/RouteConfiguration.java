package pers.acp.admin.oauth.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeTypeUtils;
import pers.acp.admin.oauth.constant.UpdateBindChannelConstant;
import pers.acp.admin.oauth.producer.UpdateRouteOutput;
import pers.acp.admin.oauth.producer.instance.UpdateRouteProducer;

import javax.annotation.PostConstruct;

/**
 * @author zhang by 17/05/2019
 * @since JDK 11
 */
@Configuration
@AutoConfigureBefore(BindingServiceConfiguration.class)
@EnableBinding(UpdateRouteOutput.class)
public class RouteConfiguration {

    private final BindingServiceProperties bindings;

    @Autowired
    public RouteConfiguration(BindingServiceProperties bindings) {
        this.bindings = bindings;
    }

    @PostConstruct
    public void init() {
        BindingProperties outputBinding = this.bindings.getBindings().get(UpdateBindChannelConstant.UPDATE_GATEWAY_OUTPUT);
        if (outputBinding == null) {
            this.bindings.getBindings().put(UpdateBindChannelConstant.UPDATE_GATEWAY_OUTPUT, new BindingProperties());
        }
        BindingProperties output = this.bindings.getBindings().get(UpdateBindChannelConstant.UPDATE_GATEWAY_OUTPUT);
        if (output.getDestination() == null || output.getDestination().equals(UpdateBindChannelConstant.UPDATE_GATEWAY_OUTPUT)) {
            output.setDestination(UpdateBindChannelConstant.UPDATE_ROUTE_DESCRIPTION);
        }
        output.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
    }

    @Bean
    public UpdateRouteProducer updateRouteProducer(UpdateRouteOutput updateRouteOutput) {
        return new UpdateRouteProducer(updateRouteOutput);
    }

}
