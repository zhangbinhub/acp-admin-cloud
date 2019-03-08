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
import pers.acp.admin.oauth.token.SecurityTokenService;
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

    private final SecurityTokenService securityTokenService;

    @Autowired
    public AuthorizationServerConfiguration(AuthenticationManager authenticationManager, SecurityUserDetailsDomain securityUserDetailsDomain, SecurityClientDetailsDomain securityClientDetailsDomain, SecurityTokenService securityTokenService) {
        this.authenticationManager = authenticationManager;
        this.securityUserDetailsDomain = securityUserDetailsDomain;
        this.securityClientDetailsDomain = securityClientDetailsDomain;
        this.securityTokenService = securityTokenService;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        securityTokenService.setClientDetailsService(securityClientDetailsDomain);
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(securityUserDetailsDomain)
                .tokenServices(securityTokenService)
                .tokenEnhancer(securityTokenService.getSecurityTokenEnhancer())
                .tokenStore(securityTokenService.getTokenStore());
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
