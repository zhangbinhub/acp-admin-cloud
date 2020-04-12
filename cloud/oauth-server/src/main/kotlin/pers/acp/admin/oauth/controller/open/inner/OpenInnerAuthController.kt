package pers.acp.admin.oauth.controller.open.inner

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthApi
import pers.acp.admin.common.vo.BooleanInfoVo
import pers.acp.admin.oauth.domain.ModuleFuncDomain
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.openInnerBasePath)
@Api(tags = ["权限信息（内部开放接口）"])
class OpenInnerAuthController @Autowired
constructor(logAdapter: LogAdapter,
            private val checkTokenEndpoint: CheckTokenEndpoint,
            private val moduleFuncDomain: ModuleFuncDomain) : BaseController(logAdapter) {
    @ApiOperation(value = "判断指定用户是否具有指定的权限")
    @GetMapping(value = [OauthApi.moduleFunc + "/{userId}/{moduleFuncCode}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun userHasModuleFunc(@PathVariable userId: String, @PathVariable moduleFuncCode: String): ResponseEntity<BooleanInfoVo> =
            ResponseEntity.ok(BooleanInfoVo(result = moduleFuncDomain.hasModuleFunc(userId, moduleFuncCode)))

    @ApiOperation(value = "校验token是否有效")
    @RequestMapping(value = [OauthApi.checkToken], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun checkToken(@RequestParam("token") value: String): ResponseEntity<Map<String, *>> =
            ResponseEntity.ok(checkTokenEndpoint.checkToken(value))
}
