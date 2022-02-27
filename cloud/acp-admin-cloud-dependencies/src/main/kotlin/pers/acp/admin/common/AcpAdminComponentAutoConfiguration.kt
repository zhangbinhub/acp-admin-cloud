package pers.acp.admin.common

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryForever
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.bus.jackson.BusJacksonAutoConfiguration
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.util.ReflectionUtils
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping
import pers.acp.admin.common.conf.ZkClientConfiguration
import pers.acp.admin.common.serialnumber.GenerateSerialNumber
import pers.acp.admin.common.serialnumber.RedisGenerateSerialNumber
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider

/**
 * @author zhang by 30/07/2019
 * @since JDK 11
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(BusJacksonAutoConfiguration::class)
@EnableConfigurationProperties(ZkClientConfiguration::class)
@RemoteApplicationEventScan(basePackageClasses = [AcpAdminComponentAutoConfiguration::class])
@Import(RedisAutoConfiguration::class)
class AcpAdminComponentAutoConfiguration {

    @Bean
    @ConditionalOnClass(RedisOperations::class)
    @ConditionalOnMissingBean(GenerateSerialNumber::class)
    fun redisGenerateSerialNumber(stringRedisTemplate: StringRedisTemplate): GenerateSerialNumber =
        RedisGenerateSerialNumber(stringRedisTemplate)

    @Bean
    @ConditionalOnClass(CuratorFramework::class)
    @ConditionalOnMissingBean(CuratorFramework::class)
    fun acpZkClient(zkClientConfiguration: ZkClientConfiguration): CuratorFramework =
        CuratorFrameworkFactory.newClient(
            zkClientConfiguration.connect,
            zkClientConfiguration.sessionTimeOut,
            zkClientConfiguration.connectionTimeOut,
            RetryForever(5000)
        ).apply { this.start() }

    @Bean
    fun springfoxHandlerProviderBeanPostProcessor(): BeanPostProcessor {
        return object : BeanPostProcessor {
            @Throws(BeansException::class)
            override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
                if (bean is WebMvcRequestHandlerProvider || bean is WebFluxRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean))
                }
                return bean
            }

            private fun customizeSpringfoxHandlerMappings(mappings: Any?) {
                if (mappings is ArrayList<*>) {
                    mappings.removeIf { mapping ->
                        if (mapping is RequestMappingInfoHandlerMapping) {
                            mapping.patternParser != null
                        } else {
                            false
                        }
                    }
                }
            }

            private fun getHandlerMappings(bean: Any): Any = try {
                ReflectionUtils.findField(bean.javaClass, "handlerMappings")?.let {
                    it.isAccessible = true
                    it[bean]
                } ?: mutableListOf<RequestMappingInfoHandlerMapping>()
            } catch (e: IllegalArgumentException) {
                throw IllegalStateException(e)
            } catch (e: IllegalAccessException) {
                throw IllegalStateException(e)
            }
        }
    }
}