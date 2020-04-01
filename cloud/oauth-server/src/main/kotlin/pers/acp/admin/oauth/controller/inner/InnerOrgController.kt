package pers.acp.admin.oauth.controller.inner

import io.swagger.annotations.*
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.api.CommonPath
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.api.OauthApi
import pers.acp.admin.common.vo.OrganizationVo
import pers.acp.admin.oauth.domain.OrganizationDomain
import pers.acp.admin.oauth.entity.Organization
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.innerBasePath)
@Api(tags = ["机构信息（内部接口）"])
class InnerOrgController @Autowired
constructor(logAdapter: LogAdapter,
            private val organizationDomain: OrganizationDomain) : BaseController(logAdapter) {
    private fun listToVo(organizationList: List<Organization>): List<OrganizationVo> =
            organizationList.map { organization ->
                OrganizationVo().apply {
                    BeanUtils.copyProperties(organization, this)
                }
            }

    @ApiOperation(value = "获取机构列表", notes = "查询所有机构列表")
    @GetMapping(value = [OauthApi.orgConfig], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun orgList(): ResponseEntity<List<OrganizationVo>> = ResponseEntity.ok(listToVo(organizationDomain.getOrgList()))

    @ApiOperation(value = "获取所属机构及其所有子机构列表")
    @GetMapping(value = [OauthApi.currAndAllChildrenOrg], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currAndAllChildrenOrgList(user: OAuth2Authentication): ResponseEntity<List<OrganizationVo>> =
            ResponseEntity.ok(listToVo(organizationDomain.getCurrAndAllChildrenOrgList(user.name)))
}