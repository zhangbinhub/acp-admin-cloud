package pers.acp.admin.oauth.controller.inner

import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.BeanUtils
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
import pers.acp.admin.common.vo.OrganizationVo
import pers.acp.admin.oauth.domain.OrganizationDomain
import pers.acp.admin.oauth.entity.Organization
import springfox.documentation.annotations.ApiIgnore

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.innerBasePath)
@Api(tags = ["机构信息（内部接口）"])
class InnerOrgController @Autowired
constructor(
    logAdapter: LogAdapter,
    private val organizationDomain: OrganizationDomain
) : BaseController(logAdapter) {
    private fun listToVo(organizationList: List<Organization>): List<OrganizationVo> =
        organizationList.map { organization ->
            OrganizationVo().apply {
                BeanUtils.copyProperties(organization, this)
            }
        }

    @ApiOperation(value = "获取所属机构及其所有子机构列表（所属机构）")
    @GetMapping(value = [OauthApi.currAndAllChildrenOrg], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currAndAllChildrenForOrg(@ApiIgnore user: OAuth2Authentication): ResponseEntity<List<OrganizationVo>> =
        ResponseEntity.ok(listToVo(organizationDomain.getCurrAndAllChildrenForOrg(user.name)))

    @ApiOperation(value = "获取所属机构及其所有子机构列表（管理机构）")
    @GetMapping(value = [OauthApi.currAndAllChildrenMngOrg], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currAndAllChildrenForMngOrg(@ApiIgnore user: OAuth2Authentication): ResponseEntity<List<OrganizationVo>> =
        ResponseEntity.ok(listToVo(organizationDomain.getCurrAndAllChildrenForMngOrg(user.name)))

    @ApiOperation(value = "获取所属机构及其所有子机构列表（所有机构）")
    @GetMapping(value = [OauthApi.currAndAllChildrenAllOrg], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Throws(ServerException::class)
    fun currAndAllChildrenForAllOrg(@ApiIgnore user: OAuth2Authentication): ResponseEntity<List<OrganizationVo>> =
        ResponseEntity.ok(listToVo(organizationDomain.getCurrAndAllChildrenForAllOrg(user.name)))
}