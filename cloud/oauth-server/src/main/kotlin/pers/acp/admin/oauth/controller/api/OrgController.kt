package pers.acp.admin.oauth.controller.api

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.OauthApi
import pers.acp.admin.oauth.constant.OrgConfigExpression
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.oauth.domain.OrganizationDomain
import pers.acp.admin.oauth.entity.Organization
import pers.acp.admin.oauth.po.OrganizationPo
import pers.acp.admin.oauth.vo.OrganizationVo
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api(tags = ["机构信息"])
class OrgController @Autowired
constructor(logAdapter: LogAdapter,
            private val organizationDomain: OrganizationDomain) : BaseController(logAdapter) {

    @ApiOperation(value = "获取机构列表", notes = "查询所有机构列表")
    @GetMapping(value = [OauthApi.orgConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun orgList(): ResponseEntity<List<Organization>> = ResponseEntity.ok(organizationDomain.getOrgList())

    @ApiOperation(value = "获取可编辑的机构列表", notes = "查询所有可编辑的机构列表")
    @GetMapping(value = [OauthApi.modifiableOrg], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun modOrgList(user: OAuth2Authentication): ResponseEntity<List<Organization>> =
            ResponseEntity.ok(organizationDomain.getModOrgList(user.name))

    @ApiOperation(value = "获取所属机构及其所有子机构列表（所属机构）")
    @GetMapping(value = [OauthApi.currAndAllChildrenOrg], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currAndAllChildrenOrgList(user: OAuth2Authentication): ResponseEntity<List<Organization>> =
            ResponseEntity.ok(organizationDomain.getCurrAndAllChildrenForOrg(user.name))

    @ApiOperation(value = "获取所属机构及其所有子机构列表（管理机构）")
    @GetMapping(value = [OauthApi.currAndAllChildrenMngOrg], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currAndAllChildrenForMngOrg(user: OAuth2Authentication): ResponseEntity<List<Organization>> =
            ResponseEntity.ok(organizationDomain.getCurrAndAllChildrenForMngOrg(user.name))

    @ApiOperation(value = "获取所属机构及其所有子机构列表（所有机构）")
    @GetMapping(value = [OauthApi.currAndAllChildrenAllOrg], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currAndAllChildrenForAllOrg(user: OAuth2Authentication): ResponseEntity<List<Organization>> =
            ResponseEntity.ok(organizationDomain.getCurrAndAllChildrenForAllOrg(user.name))

    @ApiOperation(value = "新建机构信息", notes = "名称、编码、上级ID、序号、关联用户")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = Organization::class), ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVo::class))
    @PreAuthorize(OrgConfigExpression.orgAdd)
    @PutMapping(value = [OauthApi.orgConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun add(user: OAuth2Authentication, @RequestBody @Valid organizationPo: OrganizationPo): ResponseEntity<Organization> =
            organizationDomain.doCreate(user.name, organizationPo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "删除指定的机构信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVo::class))
    @PreAuthorize(OrgConfigExpression.orgDelete)
    @DeleteMapping(value = [OauthApi.orgConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun delete(user: OAuth2Authentication,
               @ApiParam(value = "id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: MutableList<String>): ResponseEntity<InfoVo> =
            organizationDomain.doDelete(user.name, idList).let { ResponseEntity.ok(InfoVo(message = "删除成功")) }

    @ApiOperation(value = "更新机构信息", notes = "名称、编码、上级ID、序号、关联用户")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(OrgConfigExpression.orgUpdate)
    @PatchMapping(value = [OauthApi.orgConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun update(user: OAuth2Authentication, @RequestBody @Valid organizationPo: OrganizationPo): ResponseEntity<Organization> {
        if (CommonTools.isNullStr(organizationPo.id)) {
            throw ServerException("ID不能为空")
        }
        return ResponseEntity.ok(organizationDomain.doUpdate(user.name, organizationPo))
    }

    @ApiOperation(value = "获取机构详细信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(OrgConfigExpression.orgQuery)
    @GetMapping(value = [OauthApi.orgConfig + "/{orgId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun orgInfo(@ApiParam(value = "机构id", required = true)
                @NotBlank(message = "机构id不能为空")
                @PathVariable
                orgId: String): ResponseEntity<OrganizationVo> =
            ResponseEntity.ok(organizationDomain.getOrgInfo(orgId))

    @ApiOperation(value = "获取机构详细信息（编码）")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVo::class))
    @GetMapping(value = [OauthApi.orgConfig + "-by-code/{code}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun orgInfoByCode(@ApiParam(value = "机构编码", required = true)
                      @NotBlank(message = "机构编码不能为空")
                      @PathVariable
                      code: String): ResponseEntity<MutableList<OrganizationVo>> =
            ResponseEntity.ok(organizationDomain.getOrgInfoByCode(code))
}