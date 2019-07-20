package pers.acp.admin.common.conf

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisTemplate
import pers.acp.admin.common.lock.instanse.RedisDistributedLock

/**
 * @author zhang by 22/03/2019
 * @since JDK 11
 */
@Configuration
class AcpAdminCommonConfiguration {

    @Bean
    @ConditionalOnClass(RedisConnection::class)
    fun redisDistributedLock(redisTemplate: RedisTemplate<Any, Any>): RedisDistributedLock = RedisDistributedLock(redisTemplate)

}