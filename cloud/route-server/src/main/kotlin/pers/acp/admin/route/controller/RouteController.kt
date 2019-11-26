package pers.acp.admin.route.controller

import io.swagger.annotations.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pers.acp.admin.common.base.BaseController
import pers.acp.admin.common.vo.InfoVo
import pers.acp.admin.permission.BaseExpression
import pers.acp.admin.route.constant.RouteApi
import pers.acp.admin.route.domain.RouteDomain
import pers.acp.admin.route.po.RoutePo
import pers.acp.admin.route.entity.Route
import pers.acp.admin.route.po.RouteQueryPo
import pers.acp.admin.route.producer.instance.UpdateRouteProducer
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.vo.ErrorVo
import pers.acp.spring.cloud.annotation.AcpCloudDuplicateSubmission

import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(RouteApi.basePath)
@Api("网关路由信息")
class RouteController @Autowired
constructor(private val routeDomain: RouteDomain, private val updateRouteProducer: UpdateRouteProducer) : BaseController() {

    @ApiOperation(value = "新建路由信息", notes = "路由ID、路由URI、断言、过滤器、序号")
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = Route::class), ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PutMapping(value = [RouteApi.gateWayRouteConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    fun add(@RequestBody @Valid routePo: RoutePo): ResponseEntity<Route> =
            routeDomain.doCreate(routePo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "删除路由配置信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @DeleteMapping(value = [RouteApi.gateWayRouteConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun delete(@ApiParam(value = "id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: List<String>): ResponseEntity<InfoVo> {
        routeDomain.doDelete(idList)
        return ResponseEntity.ok(InfoVo(message = "删除成功"))
    }

    @ApiOperation(value = "更新路由信息", notes = "可更新路由ID、路由URI、断言、过滤器、序号")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；路由信息ID不能为空；找不到信息；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PatchMapping(value = [RouteApi.gateWayRouteConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun update(@RequestBody @Valid routePo: RoutePo): ResponseEntity<Route> {
        if (CommonTools.isNullStr(routePo.id)) {
            throw ServerException("配置ID不能为空")
        }
        routeDomain.doUpdate(routePo).let {
            return ResponseEntity.ok(it)
        }
    }

    @ApiOperation(value = "查询路由信息列表", notes = "查询条件：路由ID、是否启用")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PostMapping(value = [RouteApi.gateWayRouteConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun query(@RequestBody routeQueryPo: RouteQueryPo): ResponseEntity<Page<Route>> =
            ResponseEntity.ok(routeDomain.doQuery(routeQueryPo))

    @ApiOperation(value = "刷新路由配置信息")
    @ApiResponses(ApiResponse(code = 403, message = "没有权限执行该操作；", response = ErrorVo::class))
    @PreAuthorize(BaseExpression.superOnly)
    @PostMapping(value = [RouteApi.gateWayRouteRefresh], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun refresh(): ResponseEntity<InfoVo> {
        routeDomain.doRefresh()
        updateRouteProducer.doNotifyUpdateRoute()
        return ResponseEntity.ok(InfoVo(message = "刷新路由缓存成功，稍后网关将刷新路由配置信息"))
    }

}
