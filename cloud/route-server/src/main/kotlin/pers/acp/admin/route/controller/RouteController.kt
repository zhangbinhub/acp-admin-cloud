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
import pers.acp.admin.common.permission.BaseExpression
import pers.acp.admin.common.vo.InfoVO
import pers.acp.admin.route.constant.RouteApi
import pers.acp.admin.route.domain.RouteDomain
import pers.acp.admin.route.entity.RouteLog
import pers.acp.admin.route.po.RouteLogPo
import pers.acp.admin.route.po.RoutePo
import pers.acp.admin.route.entity.Route
import pers.acp.admin.route.producer.instance.UpdateRouteProducer
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.vo.ErrorVO
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
    @ApiResponses(ApiResponse(code = 201, message = "创建成功", response = Route::class), ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @PutMapping(value = [RouteApi.gateWayRouteConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    fun add(@RequestBody @Valid routePo: RoutePo): ResponseEntity<Route> =
            routeDomain.doCreate(routePo).let {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }

    @ApiOperation(value = "删除路由配置信息")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @DeleteMapping(value = [RouteApi.gateWayRouteConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun delete(@ApiParam(value = "id列表", required = true)
               @NotEmpty(message = "id不能为空")
               @NotNull(message = "id不能为空")
               @RequestBody
               idList: List<String>): ResponseEntity<InfoVO> {
        routeDomain.doDelete(idList)
        return ResponseEntity.ok(InfoVO(message = "删除成功"))
    }

    @ApiOperation(value = "更新路由信息", notes = "可更新路由ID、路由URI、断言、过滤器、序号")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；路由信息ID不能为空；找不到信息；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
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
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @PostMapping(value = [RouteApi.gateWayRouteConfig], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun query(@RequestBody routePo: RoutePo): ResponseEntity<Page<Route>> {
        if (routePo.queryParam == null) {
            throw ServerException("分页查询参数不能为空")
        }
        return ResponseEntity.ok(routeDomain.doQuery(routePo))
    }

    @ApiOperation(value = "查询路由日志列表", notes = "查询条件：客户端ip、网关ip、请求路径、路由服务id、开始时间、结束时间、响应状态")
    @ApiResponses(ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @PostMapping(value = [RouteApi.gateWayRouteLog], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @Throws(ServerException::class)
    fun queryLog(@RequestBody routeLogPO: RouteLogPo): ResponseEntity<Page<RouteLog>> {
        if (routeLogPO.queryParam == null) {
            throw ServerException("分页查询参数不能为空")
        }
        return ResponseEntity.ok(routeDomain.doQueryLog(routeLogPO))
    }

    @ApiOperation(value = "刷新路由配置信息")
    @ApiResponses(ApiResponse(code = 403, message = "没有权限执行该操作；", response = ErrorVO::class))
    @PreAuthorize(BaseExpression.adminOnly)
    @PostMapping(value = [RouteApi.gateWayRouteRefresh], produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    @AcpCloudDuplicateSubmission
    @Throws(ServerException::class)
    fun refresh(): ResponseEntity<InfoVO> {
        routeDomain.doRefresh()
        updateRouteProducer.doNotifyUpdateRoute()
        return ResponseEntity.ok(InfoVO(message = "刷新路由缓存成功，稍后网关将刷新路由配置信息"))
    }

}
