package pers.acp.admin.oauth.controller.inner

import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthApi
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.oauth.domain.ApplicationDomain
import pers.acp.admin.oauth.entity.Application
import springfox.documentation.annotations.ApiIgnore

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.innerBasePath)
@Api(tags = ["应用信息（内部接口）"])
class InnerApplicationController @Autowired
constructor(
    logAdapter: LogAdapter,
    private val applicationDomain: ApplicationDomain
) : BaseController(logAdapter) {

    @ApiOperation(value = "获取应用信息", notes = "根据token查询应用详细信息")
    @GetMapping(value = [OauthApi.appInfo], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun appInfo(@ApiIgnore user: OAuth2Authentication): ResponseEntity<Application> =
        applicationDomain.getApp(user.oAuth2Request.clientId)?.let {
            ResponseEntity.ok(it)
        } ?: throw ServerException("找不到应用信息")

}
