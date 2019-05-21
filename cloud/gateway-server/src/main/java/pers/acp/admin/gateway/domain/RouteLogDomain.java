package pers.acp.admin.gateway.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerWebExchange;
import pers.acp.admin.gateway.po.RouteLogPO;
import pers.acp.admin.gateway.producer.RouteLogOutput;
import pers.acp.admin.gateway.producer.instance.RouteLogProducer;

import java.net.URI;
import java.util.LinkedHashSet;

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class RouteLogDomain {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Environment environment;

    private final RouteLogOutput routeLogOutput;

    private final ObjectMapper objectMapper;

    @Autowired
    public RouteLogDomain(Environment environment, RouteLogOutput routeLogOutput, ObjectMapper objectMapper) {
        this.environment = environment;
        this.routeLogOutput = routeLogOutput;
        this.objectMapper = objectMapper;
    }

    @Bean
    public RouteLogProducer routeLogProducer() {
        return new RouteLogProducer(routeLogOutput, objectMapper);
    }

    @Transactional
    public RouteLogPO beforeRoute(ServerWebExchange serverWebExchange) {
        RouteLogPO routeLogPO = new RouteLogPO();
        routeLogPO.setRemoteIp(serverWebExchange.getRequest().getRemoteAddress() != null ? serverWebExchange.getRequest().getRemoteAddress().getHostString() : "");
        routeLogPO.setGatewayIp(environment.getProperty("server.address"));
        LinkedHashSet<URI> uris = serverWebExchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        if (uris != null) {
            for (URI uri : uris) {
                String prefix = "lb://";
                String host = environment.getProperty("server.address") + ":" + environment.getProperty("server.port");
                if (routeLogPO.getPath() == null && uri.toString().contains(host)) {
                    routeLogPO.setPath(uri.toString().substring(uri.toString().indexOf(host) + host.length()));
                } else if (routeLogPO.getServerId() == null && uri.toString().startsWith(prefix)) {
                    String s1 = uri.toString().substring(prefix.length());
                    if (s1.contains("/")) {
                        routeLogPO.setServerId(s1.substring(0, s1.indexOf("/")));
                    } else {
                        routeLogPO.setServerId(s1);
                    }
                }
            }
        }
        URI uri = serverWebExchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        if (uri != null) {
            routeLogPO.setTargetUri(uri.toString());
        }
        routeLogPO.setRequestTime(System.currentTimeMillis());
        return routeLogPO;
    }

    @Transactional
    public void afterRoute(ServerWebExchange serverWebExchange, RouteLogPO routeLogPO) {
        try {
            long responseTime = System.currentTimeMillis();
            routeLogPO.setProcessTime(responseTime - routeLogPO.getRequestTime());
            routeLogPO.setResponseTime(responseTime);
            if (serverWebExchange.getResponse().getStatusCode() != null) {
                routeLogPO.setResponseStatus(serverWebExchange.getResponse().getStatusCode().value());
            }
            routeLogProducer().doNotifyRouteLog(routeLogPO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
