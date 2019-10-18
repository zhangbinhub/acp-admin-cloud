package pers.acp.admin.common

import org.apache.curator.framework.CuratorFramework
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate
import pers.acp.admin.common.lock.RedisDistributedLock
import pers.acp.admin.common.lock.ZkDistributedLock
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.cloud.AcpCloudLogAutoConfiguration
import pers.acp.spring.cloud.lock.DistributedLock

/**
 * @author zhang by 30/09/2019
 * @since JDK 11
 */
@Configuration
@ConditionalOnMissingBean(DistributedLock::class)
@AutoConfigureAfter(AcpAdminComponentAutoConfiguration::class)
@Import(AcpCloudLogAutoConfiguration::class)
class AcpAdminDistributedLockAutoConfiguration {

    @Bean
    @ConditionalOnClass(RedisOperations::class)
    @ConditionalOnMissingBean(CuratorFramework::class)
    fun redisDistributedLock(redisTemplate: RedisTemplate<Any, Any>): DistributedLock =
            RedisDistributedLock(redisTemplate)

    @Bean
    @Primary
    @ConditionalOnBean(CuratorFramework::class)
    fun zkDistributedLock(curatorFramework: CuratorFramework, logAdapter: LogAdapter): DistributedLock =
            ZkDistributedLock(curatorFramework, logAdapter)

}