package pers.acp.admin.oauth.token

import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.core.task.timer.Calculation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenEnhancer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.constant.TokenConstant
import pers.acp.admin.oauth.repo.UserRepository

/**
 * @author zhang by 06/03/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class SecurityTokenEnhancer @Autowired
constructor(private val userRepository: UserRepository) : TokenEnhancer {

    override fun enhance(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): OAuth2AccessToken {
        if (accessToken is DefaultOAuth2AccessToken) {
            val additionalInformation: MutableMap<String, Any> = mutableMapOf()
            userRepository.findByLoginNo(authentication.name).ifPresent { user ->
                additionalInformation[TokenConstant.USER_INFO_APP_ID] = authentication.oAuth2Request.clientId
                additionalInformation[TokenConstant.USER_INFO_ID] = user.id
                additionalInformation[TokenConstant.USER_INFO_NAME] = user.name
                additionalInformation[TokenConstant.USER_INFO_LOGIN_NO] = user.loginNo
                additionalInformation[TokenConstant.USER_INFO_MOBILE] = user.mobile
                additionalInformation[TokenConstant.USER_INFO_LOGIN_TIME] =
                    CommonTools.getDateTimeString(null, Calculation.DATETIME_FORMAT)
            }
            accessToken.additionalInformation = additionalInformation
            return accessToken
        }
        return accessToken
    }
}
