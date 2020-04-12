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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthApi
import pers.acp.admin.common.vo.BooleanInfoVo
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.oauth.domain.ModuleFuncDomain
import pers.acp.admin.oauth.entity.ModuleFunc
import pers.acp.admin.oauth.token.SecurityTokenService
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.innerBasePath)
@Api(tags = ["权限信息（内部接口）"])
class InnerAuthController @Autowired
constructor(logAdapter: LogAdapter,
            private val securityTokenService: SecurityTokenService,
            private val moduleFuncDomain: ModuleFuncDomain) : BaseController(logAdapter) {
    @ApiOperation(value = "获取当前用户token信息", notes = "根据当前登录的用户token值，返回详细信息")
    @GetMapping(value = [OauthApi.currToken], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun currToken(user: OAuth2Authentication): ResponseEntity<OAuth2AccessToken> =
            ResponseEntity.ok(securityTokenService.getToken(user))

    @ApiOperation(value = "获取当前用户所有功能权限信息", notes = "根据当前登录的用户信息，查询具备的功能权限")
    @GetMapping(value = [OauthApi.currModuleFunc], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currModuleFuncList(user: OAuth2Authentication): ResponseEntity<List<ModuleFunc>> =
            ResponseEntity.ok(moduleFuncDomain.getModuleFuncList(user.oAuth2Request.clientId, user.name))

    @ApiOperation(value = "判断当前用户是否具有指定的权限")
    @GetMapping(value = [OauthApi.moduleFunc + "/{moduleFuncCode}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currUserHasModuleFunc(user: OAuth2Authentication, @PathVariable moduleFuncCode: String): ResponseEntity<BooleanInfoVo> =
            ResponseEntity.ok(BooleanInfoVo(result = moduleFuncDomain.hasModuleFunc(user.oAuth2Request.clientId, user.name, moduleFuncCode)))
}
