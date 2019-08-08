package pers.acp.admin.common

import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisTemplate
import pers.acp.admin.common.lock.RedisDistributedLock
import pers.acp.admin.common.serialnumber.GenerateSerialNumber
import pers.acp.spring.cloud.AcpCloudComponentAutoConfiguration
import pers.acp.spring.cloud.lock.DistributedLock

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration
@ConditionalOnClass(RedisConnection::class)
@AutoConfigureBefore(AcpCloudComponentAutoConfiguration::class)
class AcpAdminComponentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DistributedLock::class)
    fun redisDistributedLock(redisTemplate: RedisTemplate<Any, Any>): DistributedLock = RedisDistributedLock(redisTemplate)

    @Bean
    fun redisGenerateSerialNumber(redisTemplate: RedisTemplate<Any, Any>): GenerateSerialNumber = GenerateSerialNumber(redisTemplate)

}