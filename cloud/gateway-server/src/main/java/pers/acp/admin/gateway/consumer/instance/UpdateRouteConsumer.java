package pers.acp.admin.gateway.consumer.instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import pers.acp.admin.gateway.constant.GateWayConstant;
import pers.acp.admin.gateway.repo.RouteRedisRepository;

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
public class UpdateRouteConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RouteRedisRepository routeRedisRepository;

    @Autowired
    public UpdateRouteConsumer(RouteRedisRepository routeRedisRepository) {
        this.routeRedisRepository = routeRedisRepository;
    }

    @StreamListener(GateWayConstant.UPDATE_ROUTE_INPUT)
    public void process(String message) {
        log.info("收到 kafka 消息：" + message);
        if (GateWayConstant.UPDATE_GATEWAY_ROUTES.equals(message)) {
            log.info("开始更新路由信息...");
            routeRedisRepository.loadRouteDefinitions();
            log.info("更新路由信息完成!");
        }
    }

}
