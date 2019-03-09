package pers.acp.admin.oauth.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.constant.CommonConstant;
import pers.acp.admin.oauth.constant.TokenConstant;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.core.CommonTools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhang by 06/03/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class SecurityTokenEnhancer implements TokenEnhancer {

    private final UserRepository userRepository;

    @Autowired
    public SecurityTokenEnhancer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if (accessToken instanceof DefaultOAuth2AccessToken) {
            DefaultOAuth2AccessToken token = ((DefaultOAuth2AccessToken) accessToken);
            Map<String, Object> additionalInformation = new HashMap<>();
            userRepository.findByLoginno(authentication.getName()).ifPresent(user -> {
                additionalInformation.put(TokenConstant.USER_INFO_APPID, authentication.getOAuth2Request().getClientId());
                additionalInformation.put(TokenConstant.USER_INFO_ID, user.getId());
                additionalInformation.put(TokenConstant.USER_INFO_LOGINNO, user.getLoginno());
                additionalInformation.put(TokenConstant.USER_INFO_LOGINTIME, CommonTools.getDateTimeString(null, CommonConstant.DATE_TIME_FORMAT));
            });
            token.setAdditionalInformation(additionalInformation);
            return token;
        }
        return accessToken;
    }
}
