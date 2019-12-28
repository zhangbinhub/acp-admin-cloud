package pers.acp.admin.oauth.controller.inner

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthApi
import pers.acp.admin.oauth.domain.UserDomain
import pers.acp.admin.oauth.entity.User
import pers.acp.admin.oauth.vo.UserVo
import pers.acp.spring.boot.exceptions.ServerException
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.innerBasePath)
@Api(tags = ["用户信息（内部接口）"])
class InnerUserController @Autowired
constructor(private val userDomain: UserDomain) : BaseController() {
    @ApiOperation(value = "获取当前用户信息", notes = "根据当前登录的用户token，返回详细信息")
    @GetMapping(value = [OauthApi.currUser], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
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

    @ApiOperation(value = "查询用户信息")
    @GetMapping(value = [OauthApi.userList], params = ["!orgLevel", "roleCode"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getUserListByCurrOrgAndRole(user: OAuth2Authentication,
                                    @ApiParam(value = "角色编码", required = true)
                                    @NotBlank(message = "角色编码不能为空")
                                    @RequestParam roleCode: String): ResponseEntity<List<User>> =
            ResponseEntity.ok(userDomain.getUserListByCurrOrgAndRole(user.name, roleCode))

    @ApiOperation(value = "查询用户信息")
    @GetMapping(value = [OauthApi.userList], params = ["orgLevel", "roleCode"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getUserListByRelativeOrgAndRole(user: OAuth2Authentication,
                                        @ApiParam(value = "机构层级", required = true)
                                        @NotBlank(message = "机构层级不能为空")
                                        @RequestParam orgLevel: Int,
                                        @ApiParam(value = "角色编码", required = true)
                                        @NotBlank(message = "角色编码不能为空")
                                        @RequestParam roleCode: String): ResponseEntity<List<User>> =
            ResponseEntity.ok(userDomain.getUserListByRelativeOrgAndRole(user.name, orgLevel, roleCode))
}
