package pers.acp.springcloud.gateway.ratelimiter;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author zhangbin by 21/06/2018 11:24
 * @since JDK 11
 */
@Configuration
public class UserKeyResolverConfiguration {

    @Bean("userKeyResolver")
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getHeaders().get("Authorization")).get(0).replace(" ", "_"));
    }

}
