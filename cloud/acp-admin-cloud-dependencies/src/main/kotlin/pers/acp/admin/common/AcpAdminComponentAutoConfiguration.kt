package pers.acp.admin.common

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisTemplate
import pers.acp.admin.common.aspect.RestControllerRepeatAspect
import pers.acp.admin.common.lock.DistributedLock
import pers.acp.admin.common.lock.instanse.RedisDistributedLock

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration
class AcpAdminComponentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DistributedLock::class)
    @ConditionalOnClass(RedisConnection::class)
    fun redisDistributedLock(redisTemplate: RedisTemplate<Any, Any>): DistributedLock = RedisDistributedLock(redisTemplate)

    @Bean
    @ConditionalOnBean(DistributedLock::class)
    fun restControllerRepeatAspect(distributedLock: DistributedLock, objectMapper: ObjectMapper) =
            RestControllerRepeatAspect(distributedLock, objectMapper)

}