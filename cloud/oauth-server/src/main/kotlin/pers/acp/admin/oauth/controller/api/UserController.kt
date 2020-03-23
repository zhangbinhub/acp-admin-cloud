package pers.acp.admin.oauth.controller.api

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.OauthApi
import pers.acp.admin.oauth.constant.UserConfigExpression
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.oauth.domain.UserDomain
import pers.acp.admin.oauth.entity.User
import pers.acp.admin.oauth.po.UserInfoPo
import pers.acp.admin.oauth.po.UserPo
import pers.acp.admin.oauth.po.UserQueryPo
import pers.acp.admin.oauth.vo.UserVo
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhangbin by 11/04/2018 16:04
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api(tags = ["用户信息"])
class UserController @Autowired
constructor(private val userDomain: UserDomain) : BaseController() {

    @ApiOperation(value = "获取当前用户信息", notes = "根据当前登录的用户信息，并查询详细信息，包含用户基本信息、所属角色、所属机构")
    @ApiResponses(ApiResponse(code = 400, message = "找不到用户信息", response = ErrorVo::class))
    @GetMapping(value = [OauthApi.currUser], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun userInfo(user: OAuth2Authentication): ResponseEntity<User> =
            (userDomain.findCurrUserInfo(user.name) ?: throw ServerException("找不到用户信息")).let { ResponseEntity.ok(it) }

    @ApiOperation(value = "更新当前用户信息", notes = "1、根据当前登录的用户信息，更新头像、名称、手机；2、如果原密码和新密码均不为空，校验原密码并修改为新密码")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；找不到用户信息；原密码不正确；新密码为空；", response = ErrorVo::class))
    @RequestMapping(value = [OauthApi.currUser], method = [RequestMethod.PUT, RequestMethod.PATCH], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun updateCurrUser(user: OAuth2Authentication, @RequestBody @Valid userInfoPo: UserInfoPo): ResponseEntity<User> {
        val userInfo = userDomain.findCurrUserInfo(user.name) ?: throw ServerException("找不到用户信息")
        if (!CommonTools.isNullStr(userInfoPo.mobile)) {
            userDomain.getMobileForOtherUser(userInfoPo.mobile!!, userInfo.id)?.let { throw ServerException("手机号已存在，请重新输入") }
        }
        userInfo.avatar = userInfoPo.avatar ?: ""
        userInfo.name = userInfoPo.name ?: userInfo.name
        userInfo.mobile = userInfoPo.mobile ?: userInfo.mobile
        if (!CommonTools.isNullStr(userInfoPo.oldPassword)) {
            if (CommonTools.isNullStr(userInfoPo.password)) {
                throw ServerException("新密码为空")
            }
            if (userInfo.password.equals(userInfoPo.oldPassword!!, ignoreCase = true)) {
                userInfo.password = userInfoPo.password!!
            } else {
                throw ServerException("原密码不正确")
            }
        }
        return ResponseEntity.ok(userDomain.doSaveUser(userInfo))
    }

    @ApiOperation(value = "获取可管理的用户信息列表", notes = "根据当前登录的用户信息，获取可管理的用户信息列表")
    @ApiResponses(ApiResponse(code = 400, message = "找不到用户信息", response = ErrorVo::class))
    @PreAuthorize(UserConfigExpression.userConfig)
    @GetMapping(value = [OauthApi.modifiableUser], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun modifiableUser(user: OAuth2Authentication): ResponseEntity<List<UserVo>> =
            ResponseEntity.ok(userDomain.findModifiableUserList(user.name))

    @ApiOperation(value = "新建用户信息", notes = "名称、登录账号、手机号、级别、序号、是否启用、关联机构、管理机构、关联角色")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = User::class), ApiResponse(code = 400, message = "参数校验不通过；角色编码非法，请重新输入；", response = ErrorVo::class))
    @PreAuthorize(UserConfigExpression.userAdd)
    @PutMapping(value = [OauthApi.userConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun add(user: OAuth2Authentication, @RequestBody @Valid userPo: UserPo): ResponseEntity<User> =
            userDomain.doCreate(user.name, userPo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "删除指定的用户信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVo::class))
    @PreAuthorize(UserConfigExpression.userDelete)
    @DeleteMapping(value = [OauthApi.userConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun delete(user: OAuth2Authentication,
               @ApiParam(value = "id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: MutableList<String>): ResponseEntity<InfoVo> {
        userDomain.doDelete(user.name, idList)
        return ResponseEntity.ok(InfoVo(message = "删除成功"))
    }

    @ApiOperation(value = "更新用户信息", notes = "名称、手机号、级别、序号、是否启用、关联机构、管理机构、关联角色")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；角色编码非法，请重新输入；没有权限做此操作；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(UserConfigExpression.userUpdate)
    @PatchMapping(value = [OauthApi.userConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun update(user: OAuth2Authentication, @RequestBody @Valid userPo: UserPo): ResponseEntity<User> {
        if (CommonTools.isNullStr(userPo.id)) {
            throw ServerException("ID不能为空")
        }
        return ResponseEntity.ok(userDomain.doUpdate(user.name, userPo))
    }

    @ApiOperation(value = "重置用户密码", notes = "根据用户ID查询详细信息并重置密码")
    @ApiResponses(ApiResponse(code = 400, message = "找不到信息；", response = ErrorVo::class))
    @PreAuthorize(UserConfigExpression.userUpdate)
    @GetMapping(value = [OauthApi.userResetPwd + "/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun resetPwd(user: OAuth2Authentication, @PathVariable userId: String): ResponseEntity<InfoVo> {
        userDomain.doUpdatePwd(user.name, userId)
        return ResponseEntity.ok(InfoVo(message = "操作成功"))
    }

    @ApiOperation(value = "查询用户列表", notes = "查询条件：名称、登录帐号、状态、所属机构")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(UserConfigExpression.userQuery)
    @PostMapping(value = [OauthApi.userConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun query(@RequestBody @Valid userQueryPo: UserQueryPo): ResponseEntity<Page<UserVo>> =
            ResponseEntity.ok(userDomain.doQuery(userQueryPo))

    @ApiOperation(value = "查询用户信息", notes = "根据用户ID查询详细信息")
    @ApiResponses(ApiResponse(code = 400, message = "找不到信息；", response = ErrorVo::class))
    @PreAuthorize(UserConfigExpression.userQuery)
    @GetMapping(value = [OauthApi.userConfig + "/{userId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getUserInfo(@PathVariable userId: String): ResponseEntity<User> =
            (userDomain.getUserInfo(userId) ?: throw ServerException("找不到用户信息")).let { ResponseEntity.ok(it) }

    @ApiOperation(value = "查询用户信息")
    @PreAuthorize(UserConfigExpression.userQuery)
    @GetMapping(value = [OauthApi.userList + "/{loginNoOrName}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getUserListByLoginNoOrName(user: OAuth2Authentication,
                                   @ApiParam(value = "登录号或姓名", required = true)
                                   @NotBlank(message = "登录号或姓名不能为空")
                                   @PathVariable loginNoOrName: String): ResponseEntity<List<UserVo>> =
            ResponseEntity.ok(userDomain.getUserListByLoginNoOrName(loginNoOrName))

    @ApiOperation(value = "查询用户信息")
    @PreAuthorize(UserConfigExpression.userQuery)
    @GetMapping(value = [OauthApi.currOrgUserList], params = ["!orgLevel", "roleCode"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getUserListByCurrOrgAndRole(user: OAuth2Authentication,
                                    @ApiParam(value = "角色编码", required = true)
                                    @NotBlank(message = "角色编码不能为空")
                                    @RequestParam roleCode: String): ResponseEntity<List<UserVo>> =
            ResponseEntity.ok(userDomain.getUserListByCurrOrgAndRole(user.name, roleCode.split(",")))

    @ApiOperation(value = "查询用户信息")
    @PreAuthorize(UserConfigExpression.userQuery)
    @GetMapping(value = [OauthApi.currOrgUserList], params = ["orgLevel", "roleCode"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getUserListByRelativeOrgAndRole(user: OAuth2Authentication,
                                        @ApiParam(value = "机构层级", required = true)
                                        @NotBlank(message = "机构层级不能为空")
                                        @RequestParam orgLevel: String,
                                        @ApiParam(value = "角色编码", required = true)
                                        @NotBlank(message = "角色编码不能为空")
                                        @RequestParam roleCode: String): ResponseEntity<List<UserVo>> =
            ResponseEntity.ok(userDomain.getUserListByRelativeOrgAndRole(user.name,
                    orgLevel.split(",").map { item -> item.toInt() },
                    roleCode.split(",")))

    @ApiOperation(value = "查询用户信息")
    @PreAuthorize(UserConfigExpression.userQuery)
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
    @PreAuthorize(UserConfigExpression.userQuery)
    @GetMapping(value = [OauthApi.userList], params = ["!orgCode", "roleCode"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getUserListByRole(@ApiParam(value = "角色编码", required = true)
                          @NotBlank(message = "角色编码不能为空")
                          @RequestParam roleCode: String): ResponseEntity<List<UserVo>> =
            ResponseEntity.ok(userDomain.getUserListByRole(roleCode.split(",")))
}
