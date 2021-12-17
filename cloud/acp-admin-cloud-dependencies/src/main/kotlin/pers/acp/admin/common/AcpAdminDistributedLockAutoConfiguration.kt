package pers.acp.admin.common

import org.apache.curator.framework.CuratorFramework
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import pers.acp.admin.common.lock.ZkDistributedLock
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.cloud.AcpCloudLogAutoConfiguration
import io.github.zhangbinhub.acp.cloud.lock.DistributedLock

/**
 * @author zhang by 30/09/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(DistributedLock::class)
@AutoConfigureAfter(AcpAdminComponentAutoConfiguration::class)
@Import(AcpCloudLogAutoConfiguration::class)
class AcpAdminDistributedLockAutoConfiguration {
    @Bean
    @Primary
    @ConditionalOnBean(CuratorFramework::class)
    fun zkDistributedLock(curatorFramework: CuratorFramework, logAdapter: LogAdapter): DistributedLock =
            ZkDistributedLock(curatorFramework, logAdapter)
}