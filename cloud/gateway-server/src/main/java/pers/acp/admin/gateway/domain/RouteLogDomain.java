package pers.acp.admin.gateway.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerWebExchange;
import pers.acp.admin.gateway.entity.RouteLog;
import pers.acp.admin.gateway.repo.RouteLogRepository;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class RouteLogDomain {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Environment environment;

    private final RouteLogRepository routeLogRepository;

    private final ThreadPoolExecutor executor;

    @Autowired
    public RouteLogDomain(Environment environment, RouteLogRepository routeLogRepository) {
        this.environment = environment;
        this.routeLogRepository = routeLogRepository;
        executor = new ThreadPoolExecutor(1, 1, 60000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    @Transactional
    public Future<String> beforeRoute(ServerWebExchange serverWebExchange) {
        return executor.submit(() -> {
            RouteLog routeLog = new RouteLog();
            routeLog.setRemoteIp(serverWebExchange.getRequest().getRemoteAddress() != null ? serverWebExchange.getRequest().getRemoteAddress().getHostString() : "");
            routeLog.setGatewayIp(environment.getProperty("server.address"));
            LinkedHashSet<URI> uris = serverWebExchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
            if (uris != null) {
                for (URI uri : uris) {
                    String prefix = "lb://";
                    String host = environment.getProperty("server.address") + ":" + environment.getProperty("server.port");
                    if (routeLog.getPath() == null && uri.toString().contains(host)) {
                        routeLog.setPath(uri.toString().substring(uri.toString().indexOf(host) + host.length()));
                    } else if (routeLog.getServerId() == null && uri.toString().startsWith(prefix)) {
                        String s1 = uri.toString().substring(prefix.length());
                        if (s1.contains("/")) {
                            routeLog.setServerId(s1.substring(0, s1.indexOf("/")));
                        } else {
                            routeLog.setServerId(s1);
                        }
                    }
                }
            }
            URI uri = serverWebExchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
            if (uri != null) {
                routeLog.setTargetUri(uri.toString());
            }
            routeLog.setRequestTime(System.currentTimeMillis());
            return routeLogRepository.save(routeLog).getId();
        });
    }

    @Transactional
    public void afterRoute(ServerWebExchange serverWebExchange, Future<String> routeLogFuture) {
        executor.submit(() -> {
            try {
                String id = routeLogFuture.get();
                Optional<RouteLog> routeLogOptional = routeLogRepository.findById(id);
                if (routeLogOptional.isPresent()) {
                    RouteLog routeLog = routeLogOptional.get();
                    long responseTime = System.currentTimeMillis();
                    routeLog.setProcessTime(responseTime - routeLog.getRequestTime());
                    routeLog.setResponseTime(responseTime);
                    if (serverWebExchange.getResponse().getStatusCode() != null) {
                        routeLog.setResponseStatus(serverWebExchange.getResponse().getStatusCode().value());
                    }
                    routeLogRepository.save(routeLog);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
