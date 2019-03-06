package pers.acp.admin.oauth.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import pers.acp.admin.oauth.domain.security.SecurityClientDetailsDomain;
import pers.acp.admin.oauth.domain.security.SecurityTokenDomain;
import pers.acp.admin.oauth.domain.security.SecurityUserDetailsDomain;

/**
 * @author zhangbin by 11/04/2018 14:34
 * @since JDK 11
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    private final SecurityUserDetailsDomain securityUserDetailsDomain;

    private final SecurityClientDetailsDomain securityClientDetailsDomain;

    private final SecurityTokenDomain securityTokenDomain;

    @Autowired
    public AuthorizationServerConfiguration(AuthenticationManager authenticationManager, SecurityUserDetailsDomain securityUserDetailsDomain, SecurityClientDetailsDomain securityClientDetailsDomain, SecurityTokenDomain securityTokenDomain) {
        this.authenticationManager = authenticationManager;
        this.securityUserDetailsDomain = securityUserDetailsDomain;
        this.securityClientDetailsDomain = securityClientDetailsDomain;
        this.securityTokenDomain = securityTokenDomain;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        securityTokenDomain.setClientDetailsService(securityClientDetailsDomain);
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(securityUserDetailsDomain)
                .tokenServices(securityTokenDomain)
                .tokenEnhancer(securityTokenDomain.getSecurityTokenEnhancerDomain())
                .tokenStore(securityTokenDomain.getTokenStore());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(securityClientDetailsDomain);
    }

}
