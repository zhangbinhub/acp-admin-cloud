package pers.acp.admin.gateway.repo

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.cloud.gateway.route.RouteDefinition
import org.springframework.cloud.gateway.route.RouteDefinitionRepository
import org.springframework.context.annotation.Scope

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import pers.acp.admin.constant.RouteConstant
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import javax.annotation.PostConstruct
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class RouteRedisRepository @Autowired
constructor(private val redisTemplate: RedisTemplate<Any, Any>, private val objectMapper: ObjectMapper) : RouteDefinitionRepository {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val routes = CopyOnWriteArrayList<RouteDefinition>()

    @PostConstruct
    fun loadRouteDefinitions() {
        synchronized(this) {
            routes.clear()
            val values: MutableList<RouteDefinition> = mutableListOf()
            try {
                redisTemplate.opsForList().range(RouteConstant.ROUTES_DEFINITION_KEY, 0, -1)?.let {
                    for (route in it) {
                        values.add(objectMapper.readValue(route as ByteArray, RouteDefinition::class.java))
                    }
                }
            } catch (e: Exception) {
                log.error(e.message, e)
            }

            log.debug("redis 中路由定义条数： {}， {}", values.size, values)
            routes.addAll(values)
        }
    }

    override fun getRouteDefinitions(): Flux<RouteDefinition> {
        return Flux.fromIterable(routes)
    }

    override fun save(route: Mono<RouteDefinition>): Mono<Void> {
        return Mono.empty()
    }

    override fun delete(routeId: Mono<String>): Mono<Void> {
        return Mono.empty()
    }

}
