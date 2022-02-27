package pers.acp.admin.gateway.conf

import com.fasterxml.jackson.databind.ObjectMapper
import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.stream.config.BindingServiceConfiguration
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.web.cors.reactive.CorsUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import pers.acp.admin.constant.RouteConstant
import pers.acp.admin.gateway.constant.GateWayConstant
import pers.acp.admin.gateway.consumer.UpdateRouteConsumer
import pers.acp.admin.gateway.domain.RouteLogDomain
import pers.acp.admin.gateway.producer.RouteLogBridge
import pers.acp.admin.gateway.repo.RouteRedisRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author zhang by 17/12/2018 00:41
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(BindingServiceConfiguration::class)
class RouteConfiguration @Autowired constructor(private val routeLogDomain: RouteLogDomain) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Bean(RouteConstant.UPDATE_ROUTE_CONSUMER)
    @ConditionalOnMissingBean(name = [RouteConstant.UPDATE_ROUTE_CONSUMER])
    fun updateRouteConsumer(routeRedisRepository: RouteRedisRepository) = UpdateRouteConsumer(routeRedisRepository)

    @Bean
    fun routeLogBridge(streamBridge: StreamBridge, objectMapper: ObjectMapper) =
        RouteLogBridge(streamBridge, objectMapper, RouteConstant.ROUTE_LOG_OUTPUT)

    @Bean
    @ConditionalOnProperty(name = ["cross-domain"], havingValue = "true")
    fun corsFilter(): WebFilter {
        log.warn("cross-domain is true: Production environment is not recommended to true")
        return object : WebFilter {
            override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
                val request = exchange.request
                if (CorsUtils.isCorsRequest(request)) {
                    val response = exchange.response
                    val headers = response.headers
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN)
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS)
                    headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE)
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS)
                    headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALLOWED_Expose)
                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                    if (request.method == HttpMethod.OPTIONS) {
                        response.statusCode = HttpStatus.OK
                        return Mono.empty()
                    }
                }
                return chain.filter(exchange)
            }
        }
    }

    @Bean
    fun logRequestFilter(routeLogBridge: RouteLogBridge): GlobalFilter = GlobalFilter { exchange, chain ->
        exchange.request.mutate().headers { httpHeaders ->
            httpHeaders[GateWayConstant.GATEWAY_HEADER_REQUEST_TIME] = listOf(System.currentTimeMillis().toString())
        }.build().let {
            val webExchange = exchange.mutate().request(it).build()
            val routeLogMessage = routeLogDomain.createRouteLogMessage(webExchange)
            routeLogBridge.sendMessage(routeLogMessage)
            chain!!.filter(webExchange)
        }
    }

    @Bean
    fun logResponseFilter(routeLogBridge: RouteLogBridge): GlobalFilter = object : GlobalFilter, Ordered {
        override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
            val bufferFactory: DataBufferFactory = exchange!!.response.bufferFactory()
            val decoratedResponse: ServerHttpResponseDecorator =
                object : ServerHttpResponseDecorator(exchange.response) {
                    override fun writeWith(body: Publisher<out DataBuffer?>): Mono<Void?> {
                        val routeLogMessage = routeLogDomain.createRouteLogMessage(exchange)
                        val responseTime = System.currentTimeMillis()
                        routeLogMessage.processTime = responseTime - routeLogMessage.requestTime!!
                        routeLogMessage.responseTime = responseTime
                        exchange.response.statusCode?.let {
                            routeLogMessage.responseStatus = it.value()
                        }
                        return if (routeLogMessage.applyToken && body is Flux<*> &&
                            (exchange.response.headers.getFirst(HttpHeaders.CONTENT_TYPE)
                                ?: "").contains(MediaType.APPLICATION_JSON_VALUE, true)
                        ) {
                            val fluxBody = body as Flux<out DataBuffer?>
                            super.writeWith(fluxBody.buffer().map { dataBuffers ->
                                routeLogDomain.afterRoute(routeLogMessage, dataBuffers).let {
                                    routeLogBridge.sendMessage(routeLogMessage)
                                    bufferFactory.wrap(it)
                                }
                            })
                        } else {
                            routeLogDomain.afterRoute(routeLogMessage).let {
                                routeLogBridge.sendMessage(routeLogMessage)
                                super.writeWith(body)
                            }
                        }
                    }
                }
            return chain!!.filter(exchange.mutate().response(decoratedResponse).build())
        }

        override fun getOrder(): Int {
            return -2
        }
    }

    companion object {

        private const val ALLOWED_HEADERS =
            "Content-Type,Content-Length,Authorization,Accept,X-Requested-With,Origin,Referer,User-Agent,Process400,Process401,Process403"

        private const val ALLOWED_METHODS = "*"

        private const val ALLOWED_ORIGIN = "*"

        private const val ALLOWED_Expose = "*"

        private const val MAX_AGE = "18000L"
    }

}
