package pers.acp.admin.oauth.domain.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.config.annotation.builders.ClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.entity.Application;
import pers.acp.admin.oauth.repo.ApplicationRepository;
import pers.acp.springcloud.common.log.LogInstance;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author zhangbin by 11/04/2018 15:21
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class SecurityClientDetailsService implements ClientDetailsService {

    private final LogInstance logInstance;

    private final ApplicationRepository applicationRepository;

    private ClientDetailsService clientDetailsService = null;

    @Autowired
    public SecurityClientDetailsService(LogInstance logInstance, ApplicationRepository applicationRepository) {
        this.logInstance = logInstance;
        this.applicationRepository = applicationRepository;
    }

    /**
     * 初始化客户端信息
     */
    @PostConstruct
    public void loadClientInfo() {
        List<Application> applicationList = applicationRepository.findAll();
        InMemoryClientDetailsServiceBuilder memoryClientDetailsServiceBuilder = new InMemoryClientDetailsServiceBuilder();
        final ClientDetailsServiceBuilder.ClientBuilder[] builder = {memoryClientDetailsServiceBuilder.withClient("test")
                .secret("test")
                .authorizedGrantTypes("password", "client_credentials", "refresh_token")
                .authorities("ROLE_ADMIN")
                .scopes("test")
                .accessTokenValiditySeconds(86400)
                .refreshTokenValiditySeconds(2592000)};
        applicationList.forEach(application -> builder[0] = builder[0].and()
                .withClient(application.getId())
                .secret(application.getSecret())
                .authorizedGrantTypes("password", "client_credentials", "refresh_token")
                .scopes("ALL")
                .accessTokenValiditySeconds(application.getAccessTokenValiditySeconds())
                .refreshTokenValiditySeconds(application.getRefreshTokenValiditySeconds()));
        try {
            clientDetailsService = memoryClientDetailsServiceBuilder.build();
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
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
