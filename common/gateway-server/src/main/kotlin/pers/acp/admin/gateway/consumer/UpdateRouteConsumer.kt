package pers.acp.admin.gateway.consumer

import org.slf4j.LoggerFactory
import pers.acp.admin.constant.RouteConstant
import pers.acp.admin.gateway.repo.RouteRedisRepository
import java.util.function.Consumer

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
class UpdateRouteConsumer(private val routeRedisRepository: RouteRedisRepository) : Consumer<String> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun accept(message: String) {
        log.info("收到 kafka 消息：$message")
        if (RouteConstant.UPDATE_GATEWAY_ROUTES == message) {
            Thread {
                log.info("开始更新路由信息...")
                routeRedisRepository.loadRouteDefinitions()
                log.info("更新路由信息完成!")
            }.apply {
                this.isDaemon = true
            }.start()
        }
    }
}
