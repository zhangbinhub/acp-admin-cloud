package pers.acp.admin.oauth.controller.inner

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthApi
import pers.acp.admin.oauth.domain.ApplicationDomain
import pers.acp.admin.oauth.entity.Application
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.innerBasePath)
@Api(tags = ["应用信息（内部接口）"])
class InnerApplicationController @Autowired
constructor(private val applicationDomain: ApplicationDomain) : BaseController() {

    @ApiOperation(value = "获取应用信息", notes = "根据token查询应用详细信息")
    @GetMapping(value = [OauthApi.appInfo], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun appInfo(user: OAuth2Authentication): ResponseEntity<Application> =
            applicationDomain.getApp(user.oAuth2Request.clientId)?.let {
                ResponseEntity.ok(it)
            } ?: throw ServerException("找不到应用信息")

}
