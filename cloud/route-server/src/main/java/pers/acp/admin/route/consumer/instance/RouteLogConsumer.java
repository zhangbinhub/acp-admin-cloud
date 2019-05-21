package pers.acp.admin.route.consumer.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import pers.acp.admin.route.constant.GateWayConstant;
import pers.acp.admin.route.domain.RouteLogDomain;
import pers.acp.admin.route.entity.RouteLog;

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
public class RouteLogConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;

    private final RouteLogDomain routeLogDomain;

    @Autowired
    public RouteLogConsumer(ObjectMapper objectMapper, RouteLogDomain routeLogDomain) {
        this.objectMapper = objectMapper;
        this.routeLogDomain = routeLogDomain;
    }

    @StreamListener(GateWayConstant.ROUTE_LOG_INPUT)
    public void process(String message) {
        log.debug("收到 kafka 消息：" + message);
        try {
            routeLogDomain.doLog(objectMapper.readValue(message, RouteLog.class));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
