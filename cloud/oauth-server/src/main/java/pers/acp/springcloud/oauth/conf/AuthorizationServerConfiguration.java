package pers.acp.springcloud.oauth.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import pers.acp.springcloud.oauth.component.CustomerRedisTokenStore;
import pers.acp.springcloud.oauth.domain.SecurityClientDetailsService;
import pers.acp.springcloud.oauth.domain.SecurityUserDetailsService;

/**
 * @author zhangbin by 11/04/2018 14:34
 * @since JDK 11
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final RedisConnectionFactory connectionFactory;

    private final AuthenticationManager authenticationManager;

    private final SecurityUserDetailsService securityUserDetailsService;

    private final SecurityClientDetailsService securityClientDetailsService;

    @Autowired
    public AuthorizationServerConfiguration(AuthenticationManager authenticationManager, SecurityUserDetailsService securityUserDetailsService, SecurityClientDetailsService securityClientDetailsService, RedisConnectionFactory connectionFactory) {
        this.authenticationManager = authenticationManager;
        this.securityUserDetailsService = securityUserDetailsService;
        this.securityClientDetailsService = securityClientDetailsService;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(securityUserDetailsService)//若无，refresh_token会有UserDetailsService is required错误
//                .tokenStore(new InMemoryTokenStore());// token 默认持久化到内存
                .tokenStore(tokenStore());// token 持久化到 redis
    }

    @Bean
    public TokenStore tokenStore() {
        return new CustomerRedisTokenStore(connectionFactory);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(securityClientDetailsService);
    }

}
