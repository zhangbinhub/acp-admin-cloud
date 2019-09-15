package pers.acp.admin.oauth.controller.inner

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthInnerApi
import pers.acp.admin.oauth.domain.UserDomain
import pers.acp.admin.oauth.token.SecurityTokenService
import pers.acp.admin.oauth.vo.UserVo
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.innerBasePath)
@Api("权限信息")
class InnerAuthController @Autowired
constructor(private val userDomain: UserDomain,
            private val securityTokenService: SecurityTokenService) : BaseController() {

    @ApiOperation(value = "获取当前用户权限信息", notes = "根据当前登录的用户token，返回所有授权信息")
    @GetMapping(value = [OauthInnerApi.currOauth], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun currOauth(user: OAuth2Authentication): ResponseEntity<OAuth2Authentication> = ResponseEntity.ok(user)

    @ApiOperation(value = "获取当前用户token信息", notes = "根据当前登录的用户token值，返回详细信息")
    @GetMapping(value = [OauthInnerApi.currToken], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun currToken(user: OAuth2Authentication): ResponseEntity<OAuth2AccessToken> =
            ResponseEntity.ok(securityTokenService.getToken(user))

    @ApiOperation(value = "获取当前用户信息", notes = "根据当前登录的用户token，返回详细信息")
    @GetMapping(value = [OauthInnerApi.currUser], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun currUser(user: OAuth2Authentication): ResponseEntity<*> =
            if (user.isClientOnly) {
                UserVo().let {
                    ResponseEntity.ok(it)
                }
            } else {
                userDomain.findCurrUserInfo(user.name)?.let {
                    ResponseEntity.ok(it)
                } ?: throw ServerException("找不到用户信息")
            }

}
