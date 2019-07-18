package pers.acp.admin.config.controller

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.annotation.DuplicateSubmission
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.permission.BaseExpression
import pers.acp.admin.common.vo.InfoVO
import pers.acp.admin.config.constant.ConfigApi
import pers.acp.admin.config.domain.PropertiesDomain
import pers.acp.admin.config.entity.Properties
import pers.acp.admin.config.feign.OauthServer
import pers.acp.admin.config.po.PropertiesPo
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.vo.ErrorVO

import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(ConfigApi.basePath)
@Api("服务配置信息")
class PropertiesController @Autowired
constructor(private val propertiesDomain: PropertiesDomain, private val oauthServer: OauthServer) : BaseController() {

    @ApiOperation(value = "新建参数信息", notes = "服务名称、配置项、标签、键、值、描述")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = Properties::class), ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @PutMapping(value = [ConfigApi.propertiesConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun add(@RequestBody @Valid propertiesPo: PropertiesPo): ResponseEntity<Properties> =
            propertiesDomain.doCreate(propertiesPo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "删除指定的服务配置信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @DeleteMapping(value = [ConfigApi.propertiesConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun delete(@ApiParam(value = "id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: List<String>): ResponseEntity<InfoVO> {
        propertiesDomain.doDelete(idList)
        return ResponseEntity.ok(InfoVO(
                message = "删除成功"
        ))
    }

    @ApiOperation(value = "更新指定的参数信息", notes = "可更新服务名称、配置项、标签、键、值、描述")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；配置ID不能为空；找不到信息；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @PatchMapping(value = [ConfigApi.propertiesConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun update(@RequestBody @Valid propertiesPo: PropertiesPo): ResponseEntity<Properties> =
            if (CommonTools.isNullStr(propertiesPo.id)) {
                throw ServerException("配置ID不能为空")
            } else {
                propertiesDomain.doUpdate(propertiesPo).let {
                    ResponseEntity.ok(it)
                }
            }

    @ApiOperation(value = "查询参数信息列表", notes = "查询条件：服务名称、配置项、标签、键、状态")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @PostMapping(value = [ConfigApi.propertiesConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun query(@RequestBody propertiesPo: PropertiesPo): ResponseEntity<Page<Properties>> =
            if (propertiesPo.queryParam == null) {
                throw ServerException("分页查询参数不能为空")
            } else {
                ResponseEntity.ok(propertiesDomain.doQuery(propertiesPo))
            }

    @ApiOperation(value = "刷新配置信息")
    @ApiResponses(ApiResponse(code = 403, message = "没有权限执行该操作；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @PostMapping(value = [ConfigApi.propertiesRefresh], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun refresh(): ResponseEntity<InfoVO> {
        oauthServer.busRefresh()
        return ResponseEntity.ok(InfoVO(
                message = "请求成功，稍后所有服务将刷新配置信息"
        ))
    }

}
