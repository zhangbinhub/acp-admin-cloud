package pers.acp.admin.common

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryForever
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisTemplate
import pers.acp.admin.common.conf.ZkClientConfiguration
import pers.acp.admin.common.serialnumber.GenerateSerialNumber
import pers.acp.spring.cloud.AcpCloudComponentAutoConfiguration

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration
@AutoConfigureBefore(AcpCloudComponentAutoConfiguration::class)
@EnableConfigurationProperties(ZkClientConfiguration::class)
class AcpAdminComponentAutoConfiguration {

    @Bean
    @ConditionalOnClass(RedisConnection::class)
    fun redisGenerateSerialNumber(redisTemplate: RedisTemplate<Any, Any>): GenerateSerialNumber = GenerateSerialNumber(redisTemplate)

    @Bean
    @ConditionalOnClass(CuratorFramework::class)
    @ConditionalOnMissingBean(CuratorFramework::class)
    fun acpZkClient(zkClientConfiguration: ZkClientConfiguration): CuratorFramework =
            CuratorFrameworkFactory.newClient(
                    zkClientConfiguration.connect,
                    zkClientConfiguration.sessionTimeOut,
                    zkClientConfiguration.connectionTimeOut,
                    RetryForever(5000)).apply { this.start() }

}