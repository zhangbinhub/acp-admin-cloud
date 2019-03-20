package pers.acp.admin.gateway.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import pers.acp.admin.gateway.constant.GateWayConstant;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RouteRedisRepository implements RouteDefinitionRepository {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<RouteDefinition> routes = new CopyOnWriteArrayList<>();

    private final RedisConnectionFactory connectionFactory;

    private final ObjectMapper objectMapper;

    @Autowired
    public RouteRedisRepository(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        this.connectionFactory = connectionFactory;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadRouteDefinitions() {
        synchronized (this) {
            routes.clear();
            List<RouteDefinition> values = new ArrayList<>();
            RedisConnection connection = connectionFactory.getConnection();
            try {
                List<byte[]> routeList = connection.lRange(GateWayConstant.ROUTES_DEFINITION_KEY.getBytes(), 0, -1);
                if (routeList != null) {
                    for (byte[] route : routeList) {
                        values.add(objectMapper.readValue(route, RouteDefinition.class));
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                connection.close();
            }
            log.debug("redis 中路由定义条数： {}， {}", values.size(), values);
            routes.addAll(values);
        }
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(routes);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return Mono.empty();
    }

}
