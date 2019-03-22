package pers.acp.admin.common.conf;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import pers.acp.admin.common.lock.instanse.RedisDistributedLock;

/**
 * @author zhang by 22/03/2019
 * @since JDK 11
 */
@Configuration
public class AcpAdminCommonConfiguration {

    @Bean
    @ConditionalOnClass(RedisConnectionFactory.class)
    public RedisDistributedLock redisDistributedLock(RedisConnectionFactory redisConnectionFactory) {
        return new RedisDistributedLock(redisConnectionFactory);
    }

}
