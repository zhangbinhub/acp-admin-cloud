package pers.acp.admin.gateway.ratelimiter

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import reactor.core.publisher.Mono

import java.util.Objects

/**
 * @author zhangbin by 21/06/2018 11:24
 * @since JDK 11
 */
@Configuration
class UserKeyResolverConfiguration {

    @Bean("userKeyResolver")
    fun userKeyResolver() = KeyResolver { exchange ->
        Mono.just(Objects.requireNonNull<List<String>>(exchange.request.headers[HttpHeaders.AUTHORIZATION])[0].replace(" ", "_"))
    }

}
