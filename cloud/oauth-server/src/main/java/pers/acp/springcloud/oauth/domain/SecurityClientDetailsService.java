package pers.acp.springcloud.oauth.domain;

import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import pers.acp.core.log.LogFactory;

import javax.annotation.PostConstruct;

/**
 * @author zhangbin by 11/04/2018 15:21
 * @since JDK 11
 */
@Service
public class SecurityClientDetailsService implements ClientDetailsService {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private ClientDetailsService clientDetailsService = null;

    /**
     * 初始化客户端信息
     */
    @PostConstruct
    public void init() {
        InMemoryClientDetailsServiceBuilder builder = new InMemoryClientDetailsServiceBuilder();
        builder.withClient("test")
                .secret("test")
                .authorizedGrantTypes("password", "client_credentials", "refresh_token")
                .authorities("ROLE_ADMIN")
                .scopes("ALL")
                .accessTokenValiditySeconds(600)
                .refreshTokenValiditySeconds(86400)
                .and()
                .withClient("client")
                .secret("client")
                .authorizedGrantTypes("password", "client_credentials", "refresh_token")
                .authorities("ROLE_ADMIN")
                .scopes("ALL")
                .accessTokenValiditySeconds(600)
                .refreshTokenValiditySeconds(86400);
        try {
            clientDetailsService = builder.build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        if (clientDetailsService != null) {
            return clientDetailsService.loadClientByClientId(clientId);
        } else {
            return null;
        }
    }

}
