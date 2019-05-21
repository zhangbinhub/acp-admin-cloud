package pers.acp.admin.route.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeTypeUtils;
import pers.acp.admin.route.constant.GateWayConstant;
import pers.acp.admin.route.consumer.RouteLogInput;
import pers.acp.admin.route.consumer.instance.RouteLogConsumer;
import pers.acp.admin.route.domain.RouteLogDomain;
import pers.acp.admin.route.producer.UpdateRouteOutput;
import pers.acp.admin.route.producer.instance.UpdateRouteProducer;

import javax.annotation.PostConstruct;

/**
 * @author zhang by 17/05/2019
 * @since JDK 11
 */
@Configuration
@AutoConfigureBefore(BindingServiceConfiguration.class)
@EnableBinding({UpdateRouteOutput.class, RouteLogInput.class})
public class RouteConfiguration {

    private final BindingServiceProperties bindings;

    @Autowired
    public RouteConfiguration(BindingServiceProperties bindings) {
        this.bindings = bindings;
    }

    @PostConstruct
    public void init() {
        initProducer();
        initConsumer();
    }

    private void initProducer() {
        BindingProperties outputBinding = this.bindings.getBindings().get(GateWayConstant.UPDATE_GATEWAY_OUTPUT);
        if (outputBinding == null) {
            this.bindings.getBindings().put(GateWayConstant.UPDATE_GATEWAY_OUTPUT, new BindingProperties());
        }
        BindingProperties output = this.bindings.getBindings().get(GateWayConstant.UPDATE_GATEWAY_OUTPUT);
        if (output.getDestination() == null || output.getDestination().equals(GateWayConstant.UPDATE_GATEWAY_OUTPUT)) {
            output.setDestination(GateWayConstant.UPDATE_ROUTE_DESCRIPTION);
        }
        output.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
    }

    private void initConsumer() {
        BindingProperties inputBinding = this.bindings.getBindings().get(GateWayConstant.ROUTE_LOG_INPUT);
        if (inputBinding == null) {
            this.bindings.getBindings().put(GateWayConstant.ROUTE_LOG_INPUT, new BindingProperties());
        }
        BindingProperties input = this.bindings.getBindings().get(GateWayConstant.ROUTE_LOG_INPUT);
        if (input.getDestination() == null || input.getDestination().equals(GateWayConstant.ROUTE_LOG_INPUT)) {
            input.setDestination(GateWayConstant.ROUTE_LOG_DESCRIPTION);
        }
        input.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        input.setGroup(GateWayConstant.ROUTE_LOG_CONSUMER_GROUP);
    }

    @Bean
    public UpdateRouteProducer updateRouteProducer(UpdateRouteOutput updateRouteOutput) {
        return new UpdateRouteProducer(updateRouteOutput);
    }

    @Bean
    public RouteLogConsumer updateRouteConsumer(ObjectMapper objectMapper, RouteLogDomain routeLogDomain) {
        return new RouteLogConsumer(objectMapper, routeLogDomain);
    }

}
