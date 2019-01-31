package pers.acp.admin.oauth.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import pers.acp.admin.oauth.domain.security.SecurityClientDetailsService;
import pers.acp.admin.oauth.domain.security.SecurityTokenService;
import pers.acp.admin.oauth.domain.security.SecurityUserDetailsService;

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
    public AuthorizationServerConfiguration(RedisConnectionFactory connectionFactory, AuthenticationManager authenticationManager, SecurityUserDetailsService securityUserDetailsService, SecurityClientDetailsService securityClientDetailsService) {
        this.connectionFactory = connectionFactory;
        this.authenticationManager = authenticationManager;
        this.securityUserDetailsService = securityUserDetailsService;
        this.securityClientDetailsService = securityClientDetailsService;
    }

    private SecurityTokenService securityTokenService() {
        return new SecurityTokenService();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        SecurityTokenService securityTokenService = securityTokenService();
        // token 默认持久化到内存
//        TokenStore tokenStore = new InMemoryTokenStore();
        // 持久化到 Redis
        TokenStore tokenStore = new RedisTokenStore(connectionFactory);
        securityTokenService.setTokenStore(tokenStore);
        securityTokenService.setClientDetailsService(securityClientDetailsService);
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(securityUserDetailsService)
                .tokenServices(securityTokenService)
                .tokenStore(tokenStore);
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
