package pers.acp.admin.oauth.controller.open.inner

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.CommonPath
import pers.acp.admin.api.OauthApi
import pers.acp.admin.oauth.domain.UserDomain
import pers.acp.admin.oauth.vo.UserVo
import pers.acp.spring.boot.exceptions.ServerException
import javax.validation.constraints.NotBlank

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.openInnerBasePath)
@Api(tags = ["用户列表（内部开放接口）"])
class OpenInnerUserController @Autowired
constructor(private val userDomain: UserDomain) : BaseController() {
    @ApiOperation(value = "查询用户信息")
    @GetMapping(value = [OauthApi.userList], params = ["orgCode", "roleCode"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getUserListByOrgCodeAndRole(@ApiParam(value = "机构编码", required = true)
                                    @NotBlank(message = "机构编码不能为空")
                                    @RequestParam orgCode: String,
                                    @ApiParam(value = "角色编码", required = true)
                                    @NotBlank(message = "角色编码不能为空")
                                    @RequestParam roleCode: String): ResponseEntity<List<UserVo>> =
            ResponseEntity.ok(userDomain.getUserListByOrgCodeAndRole(orgCode.split(","), roleCode.split(",")))

    @ApiOperation(value = "查询用户信息")
    @GetMapping(value = [OauthApi.userList], params = ["!orgCode", "roleCode"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getUserListByRole(@ApiParam(value = "角色编码", required = true)
                          @NotBlank(message = "角色编码不能为空")
                          @RequestParam roleCode: String): ResponseEntity<List<UserVo>> =
            ResponseEntity.ok(userDomain.getUserListByRole(roleCode.split(",")))
}
