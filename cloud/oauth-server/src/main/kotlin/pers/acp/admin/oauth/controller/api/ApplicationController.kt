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
import pers.acp.admin.oauth.constant.AppConfigExpression
import pers.acp.admin.common.vo.InfoVO
import pers.acp.admin.oauth.constant.OauthApi
import pers.acp.admin.oauth.bus.publish.RefreshEventPublish
import pers.acp.admin.oauth.domain.ApplicationDomain
import pers.acp.admin.oauth.entity.Application
import pers.acp.admin.oauth.po.ApplicationPo
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.vo.ErrorVO
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api("应用信息")
class ApplicationController @Autowired
constructor(private val applicationDomain: ApplicationDomain, private val refreshEventPublish: RefreshEventPublish) : BaseController() {

    @ApiOperation(value = "新建应用信息", notes = "应用名称、token 有效期、refresh token 有效期")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = Application::class), ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(AppConfigExpression.appAdd)
    @PutMapping(value = [OauthApi.appConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    fun add(@RequestBody @Valid applicationPO: ApplicationPo): ResponseEntity<Application> =
            applicationDomain.doCreate(applicationPO).also {
                refreshEventPublish.doNotifyUpdateApp()
            }.let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "删除指定的信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(AppConfigExpression.appDelete)
    @DeleteMapping(value = [OauthApi.appConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun delete(@ApiParam(value = "id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: MutableList<String>): ResponseEntity<InfoVO> {
        applicationDomain.doDelete(idList)
        refreshEventPublish.doNotifyUpdateApp()
        return ResponseEntity.ok(InfoVO(message = "删除成功"))
    }

    @ApiOperation(value = "更新指定的信息", notes = "可更新应用名称、token 有效期、refresh token 有效期")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO::class))
    @PreAuthorize(AppConfigExpression.appUpdate)
    @PatchMapping(value = [OauthApi.appConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun update(@RequestBody @Valid applicationPO: ApplicationPo): ResponseEntity<Application> {
        if (CommonTools.isNullStr(applicationPO.id)) {
            throw ServerException("ID不能为空")
        }
        return applicationDomain.doUpdate(applicationPO).also {
            refreshEventPublish.doNotifyUpdateApp()
        }.let {
            ResponseEntity.ok(it)
        }
    }

    @ApiOperation(value = "查询信息列表", notes = "查询条件：应用名称")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(AppConfigExpression.appQuery)
    @PostMapping(value = [OauthApi.appConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun query(@RequestBody applicationPO: ApplicationPo): ResponseEntity<Page<Application>> =
            (applicationPO.queryParam ?: throw ServerException("分页查询参数不能为空")).let {
                return ResponseEntity.ok(applicationDomain.doQuery(applicationPO))
            }

    @ApiOperation(value = "获取应用列表", notes = "查询所有应用列表")
    @PreAuthorize(AppConfigExpression.sysConfig)
    @GetMapping(value = [OauthApi.appConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun appList(user: OAuth2Authentication): ResponseEntity<List<Application>> = ResponseEntity.ok(applicationDomain.getAppList(user))

    @ApiOperation(value = "更新应用密钥")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO::class))
    @PreAuthorize(AppConfigExpression.appUpdateSecret)
    @GetMapping(value = [OauthApi.updateSecret + "/{appId}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun updateSecret(@ApiParam(value = "应用id", required = true)
                     @NotBlank(message = "应用id不能为空")
                     @PathVariable
                     appId: String): ResponseEntity<Application> =
            applicationDomain.doUpdateSecret(appId).also {
                refreshEventPublish.doNotifyUpdateApp()
            }.let {
                ResponseEntity.ok(it)
            }

}
