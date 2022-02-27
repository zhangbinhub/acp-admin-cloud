package pers.acp.admin.oauth.controller.api

import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.boot.vo.ErrorVo
import io.github.zhangbinhub.acp.cloud.annotation.AcpCloudDuplicateSubmission
import io.github.zhangbinhub.acp.core.CommonTools
import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.api.OauthApi
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.oauth.constant.RoleConfigExpression
import pers.acp.admin.oauth.domain.RoleDomain
import pers.acp.admin.oauth.entity.Role
import pers.acp.admin.oauth.po.RolePo
import pers.acp.admin.oauth.vo.RoleVo
import pers.acp.admin.permission.BaseExpression
import springfox.documentation.annotations.ApiIgnore
import javax.annotation.PostConstruct
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api(tags = ["角色信息"])
class RoleController @Autowired
constructor(
    private val logAdapter: LogAdapter,
    private val roleDomain: RoleDomain
) : BaseController(logAdapter) {

    private val roleCodeList: MutableList<String> = mutableListOf()

    @ApiOperation(value = "获取角色编码列表")
    @PreAuthorize(RoleConfigExpression.roleConfig)
    @GetMapping(value = [OauthApi.roleCodes], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findRoleCode(): ResponseEntity<List<String>> = ResponseEntity.ok(roleCodeList)

    @PostConstruct
    fun init() {
        try {
            for (field in RoleCode::class.java.declaredFields) {
                val value = field.get(RoleCode::class.java)
                if (value is String) {
                    if (RoleCode.prefix != value) {
                        roleCodeList.add(value)
                    }
                }
            }
            roleCodeList.sort()
        } catch (e: Exception) {
            logAdapter.error(e.message, e)
        }

    }

    @ApiOperation(value = "获取指定应用下可编辑的角色列表", notes = "查询指定应用下可编辑的角色列表")
    @PreAuthorize(BaseExpression.sysConfig)
    @GetMapping(value = [OauthApi.roleList + "/{appId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun getRoleList(@ApiIgnore user: OAuth2Authentication, @PathVariable appId: String): ResponseEntity<List<Role>> =
        ResponseEntity.ok(roleDomain.getRoleListByAppId(user.name, appId))

    @ApiOperation(value = "获取角色列表", notes = "查询所有角色列表")
    @PreAuthorize(RoleConfigExpression.roleQuery)
    @GetMapping(value = [OauthApi.roleConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun roleList(): ResponseEntity<List<Role>> = ResponseEntity.ok(roleDomain.getRoleList())

    @ApiOperation(value = "新建角色信息", notes = "名称、编码、应用ID、级别、序号、关联用户、关联菜单、关联模块功能")
    @ApiResponses(
        ApiResponse(code = 201, message = "创建成功", response = Role::class),
        ApiResponse(code = 400, message = "参数校验不通过；角色编码非法，请重新输入；", response = ErrorVo::class)
    )
    @PreAuthorize(RoleConfigExpression.roleAdd)
    @PutMapping(value = [OauthApi.roleConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun add(@ApiIgnore user: OAuth2Authentication, @RequestBody @Valid rolePo: RolePo): ResponseEntity<Role> {
        if (CommonTools.isNullStr(rolePo.appId)) {
            throw ServerException("应用ID不能为空")
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(roleDomain.doCreate(user.name, rolePo))
    }

    @ApiOperation(value = "删除指定的角色信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVo::class))
    @PreAuthorize(RoleConfigExpression.roleDelete)
    @DeleteMapping(value = [OauthApi.roleConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun delete(
        @ApiIgnore user: OAuth2Authentication,
        @ApiParam(value = "id列表", required = true)
        @NotEmpty(message = "id不能为空")
        @NotNull(message = "id不能为空")
        @RequestBody
        idList: MutableList<String>
    ): ResponseEntity<InfoVo> =
        roleDomain.doDelete(user.name, idList).let { ResponseEntity.ok(InfoVo(message = "删除成功")) }

    @ApiOperation(value = "更新角色信息", notes = "名称、编码、级别、序号、关联用户、关联菜单、关联模块功能")
    @ApiResponses(
        ApiResponse(
            code = 400,
            message = "参数校验不通过；角色编码非法，请重新输入；没有权限做此操作；ID不能为空；找不到信息；",
            response = ErrorVo::class
        )
    )
    @PreAuthorize(RoleConfigExpression.roleUpdate)
    @PatchMapping(value = [OauthApi.roleConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun update(@ApiIgnore user: OAuth2Authentication, @RequestBody @Valid rolePo: RolePo): ResponseEntity<Role> {
        if (CommonTools.isNullStr(rolePo.id)) {
            throw ServerException("ID不能为空")
        }
        return ResponseEntity.ok(roleDomain.doUpdate(user.name, rolePo))
    }

    @ApiOperation(value = "获取角色详细信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(RoleConfigExpression.roleQuery)
    @GetMapping(value = [OauthApi.roleConfig + "/{roleId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun orgInfo(@PathVariable roleId: String): ResponseEntity<RoleVo> =
        ResponseEntity.ok(roleDomain.getRoleInfo(roleId))

}
