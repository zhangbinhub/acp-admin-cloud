package pers.acp.admin.gateway.producer.instance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import pers.acp.admin.gateway.po.RouteLogPO;
import pers.acp.admin.gateway.producer.RouteLogOutput;

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
public class RouteLogProducer {

    private final RouteLogOutput routeLogOutput;

    private final ObjectMapper objectMapper;

    @Autowired
    public RouteLogProducer(RouteLogOutput routeLogOutput, ObjectMapper objectMapper) {
        this.routeLogOutput = routeLogOutput;
        this.objectMapper = objectMapper;
    }

    public void doNotifyRouteLog(RouteLogPO routeLogPO) throws JsonProcessingException {
        routeLogOutput.sendMessage().send(MessageBuilder.withPayload(objectMapper.writeValueAsString(routeLogPO)).build());
    }

}
