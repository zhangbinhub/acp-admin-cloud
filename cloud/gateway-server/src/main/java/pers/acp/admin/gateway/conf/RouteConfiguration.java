package pers.acp.admin.gateway.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import pers.acp.admin.gateway.constant.GateWayConstant;
import pers.acp.admin.gateway.consumer.UpdateRouteInput;
import pers.acp.admin.gateway.consumer.instance.UpdateRouteConsumer;
import pers.acp.admin.gateway.domain.RouteDomain;
import pers.acp.admin.gateway.repo.RouteRedisRepository;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

/**
 * @author zhang by 17/12/2018 00:41
 * @since JDK 11
 */
@Configuration
@AutoConfigureBefore(BindingServiceConfiguration.class)
@EnableBinding(UpdateRouteInput.class)
public class RouteConfiguration {

    private static final String ALLOWED_HEADERS = "Content-Type,Content-Length,Authorization,Accept,X-Requested-With,Origin,Referer,User-Agent,Process400,Process401,Process403";

    private static final String ALLOWED_METHODS = "*";

    private static final String ALLOWED_ORIGIN = "*";

    private static final String ALLOWED_Expose = "*";

    private static final String MAX_AGE = "18000L";

    private final Environment environment;

    private final BindingServiceProperties bindings;

    private final RouteDomain routeDomain;

    @Autowired
    public RouteConfiguration(Environment environment, BindingServiceProperties bindings, RouteDomain routeDomain) {
        this.environment = environment;
        this.bindings = bindings;
        this.routeDomain = routeDomain;
    }

    @PostConstruct
    public void init() {
        BindingProperties inputBinding = this.bindings.getBindings().get(GateWayConstant.UPDATE_ROUTE_INPUT);
        if (inputBinding == null) {
            this.bindings.getBindings().put(GateWayConstant.UPDATE_ROUTE_INPUT, new BindingProperties());
        }
        BindingProperties input = this.bindings.getBindings().get(GateWayConstant.UPDATE_ROUTE_INPUT);
        if (input.getDestination() == null || input.getDestination().equals(GateWayConstant.UPDATE_ROUTE_INPUT)) {
            input.setDestination(GateWayConstant.UPDATE_ROUTE_DESCRIPTION);
        }
        input.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        input.setGroup(GateWayConstant.UPDATE_ROUTE_GROUP_PREFIX + environment.getProperty("server.address") + "-" + environment.getProperty("server.port"));
    }

    @Bean
    public UpdateRouteConsumer updateRouteConsumer(RouteRedisRepository routeRedisRepository) {
        return new UpdateRouteConsumer(routeRedisRepository);
    }

    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            if (CorsUtils.isCorsRequest(request)) {
                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN);
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
                headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS);
                headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALLOWED_Expose);
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }

    @Bean
    public GlobalFilter countFilter() {
        return (exchange, chain) -> {
            routeDomain.beforeRoute(exchange.getRequest());
            return chain.filter(exchange)
                    .then(Mono.just(exchange))
                    .map(serverWebExchange -> {
                        routeDomain.afterRoute(exchange.getResponse());
                        return serverWebExchange;
                    })
                    .then();
        };
    }
}
