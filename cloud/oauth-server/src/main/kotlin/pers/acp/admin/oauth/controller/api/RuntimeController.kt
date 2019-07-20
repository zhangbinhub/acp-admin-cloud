package pers.acp.admin.oauth.controller.api

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
import pers.acp.admin.oauth.constant.RuntimeConfigExpression
import pers.acp.admin.common.vo.InfoVO
import pers.acp.admin.common.vo.RuntimeConfigVO
import pers.acp.admin.oauth.constant.OauthApi
import pers.acp.admin.oauth.bus.publish.RefreshEventPublish
import pers.acp.admin.oauth.controller.inner.InnerRuntimeController
import pers.acp.admin.oauth.domain.RuntimeConfigDomain
import pers.acp.admin.oauth.entity.RuntimeConfig
import pers.acp.admin.oauth.po.RuntimePo
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.vo.ErrorVO

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api("运行参数配置")
class RuntimeController @Autowired
constructor(private val innerRuntimeController: InnerRuntimeController, private val runtimeConfigDomain: RuntimeConfigDomain, private val refreshEventPublish: RefreshEventPublish) : BaseController() {

    @ApiOperation(value = "新建参数信息", notes = "参数名称、参数值、描述、状态")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = RuntimeConfig::class), ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVO::class))
    @PreAuthorize(RuntimeConfigExpression.runtimeAdd)
    @PutMapping(value = [OauthApi.runtimeConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun add(@RequestBody @Valid runtimePO: RuntimePo): ResponseEntity<RuntimeConfig> =
            runtimeConfigDomain.doCreate(runtimePO).also {
                refreshEventPublish.doNotifyUpdateRuntime()
            }.let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "删除指定的参数信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(RuntimeConfigExpression.runtimeDelete)
    @DeleteMapping(value = [OauthApi.runtimeConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun delete(@ApiParam(value = "id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: MutableList<String>): ResponseEntity<InfoVO> =
            runtimeConfigDomain.doDelete(idList).also {
                refreshEventPublish.doNotifyUpdateRuntime()
            }.let {
                ResponseEntity.ok(InfoVO(message = "删除成功"))
            }

    @ApiOperation(value = "更新指定的参数信息", notes = "可更新参数值、描述、状态")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；配置ID不能为空；找不到信息；", response = ErrorVO::class))
    @PreAuthorize(RuntimeConfigExpression.runtimeUpdate)
    @PatchMapping(value = [OauthApi.runtimeConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @DuplicateSubmission
    @Throws(ServerException::class)
    fun update(@RequestBody @Valid runtimePO: RuntimePo): ResponseEntity<RuntimeConfig> {
        if (CommonTools.isNullStr(runtimePO.id)) {
            throw ServerException("配置ID不能为空")
        }
        return runtimeConfigDomain.doUpdate(runtimePO).also {
            refreshEventPublish.doNotifyUpdateRuntime()
        }.let {
            ResponseEntity.ok(it)
        }
    }

    @ApiOperation(value = "查询参数信息列表", notes = "查询条件：参数名称、值、状态")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(RuntimeConfigExpression.runtimeQuery)
    @PostMapping(value = [OauthApi.runtimeConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun query(@RequestBody runtimePO: RuntimePo): ResponseEntity<Page<RuntimeConfig>> =
            (runtimePO.queryParam ?: throw ServerException("分页查询参数不能为空")).let {
                ResponseEntity.ok(runtimeConfigDomain.doQuery(runtimePO))
            }

    @ApiOperation(value = "获取参数信息", notes = "根据参数名称获取")
    @ApiResponses(ApiResponse(code = 400, message = "找不到参数信息；", response = ErrorVO::class))
    @GetMapping(value = [OauthApi.runtimeConfig + "/{name}"], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun find(@ApiParam(value = "参数名称", required = true)
             @NotBlank(message = "参数名称不能为空")
             @PathVariable
             name: String): ResponseEntity<RuntimeConfigVO> = innerRuntimeController.find(name)

}