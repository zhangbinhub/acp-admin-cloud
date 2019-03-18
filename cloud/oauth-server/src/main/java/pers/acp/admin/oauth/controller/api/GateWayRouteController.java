package pers.acp.admin.oauth.controller.api;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.path.oauth.OauthApi;
import pers.acp.admin.common.permission.BaseExpression;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.oauth.domain.GateWayRouteDomain;
import pers.acp.admin.oauth.entity.GateWayRoute;
import pers.acp.admin.oauth.po.GateWayRoutePO;
import pers.acp.admin.oauth.producer.instance.UpdateRouteProducer;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api("网关路由信息")
public class GateWayRouteController extends BaseController {

    private final GateWayRouteDomain gateWayRouteDomain;

    private final UpdateRouteProducer updateRouteProducer;

    @Autowired
    public GateWayRouteController(GateWayRouteDomain gateWayRouteDomain, UpdateRouteProducer updateRouteProducer) {
        this.gateWayRouteDomain = gateWayRouteDomain;
        this.updateRouteProducer = updateRouteProducer;
    }

    @ApiOperation(value = "新建路由信息",
            notes = "路由ID、路由URI、断言、过滤器、序号")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = GateWayRoute.class),
            @ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @PutMapping(value = OauthApi.gateWayRouteConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GateWayRoute> add(@RequestBody @Valid GateWayRoutePO gateWayRoutePO) throws ServerException {
        GateWayRoute gateWayRoute = gateWayRouteDomain.doCreate(gateWayRoutePO);
        return ResponseEntity.status(HttpStatus.CREATED).body(gateWayRoute);
    }

    @ApiOperation(value = "删除路由配置信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @DeleteMapping(value = OauthApi.gateWayRouteConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> delete(@ApiParam(value = "id列表", required = true) @NotEmpty(message = "id不能为空") @NotNull(message = "id不能为空")
                                         @RequestBody List<String> idList) {
        gateWayRouteDomain.doDelete(idList);
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "更新路由信息",
            notes = "可更新路由ID、路由URI、断言、过滤器、序号")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；路由信息ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @PatchMapping(value = OauthApi.gateWayRouteConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GateWayRoute> update(@RequestBody @Valid GateWayRoutePO gateWayRoutePO) throws ServerException {
        if (CommonTools.isNullStr(gateWayRoutePO.getId())) {
            throw new ServerException("配置ID不能为空");
        }
        GateWayRoute gateWayRoute = gateWayRouteDomain.doUpdate(gateWayRoutePO);
        return ResponseEntity.ok(gateWayRoute);
    }

    @ApiOperation(value = "查询路由信息列表",
            notes = "查询条件：路由ID、是否启用")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @PostMapping(value = OauthApi.gateWayRouteConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<GateWayRoute>> query(@RequestBody GateWayRoutePO gateWayRoutePO) throws ServerException {
        if (gateWayRoutePO.getQueryParam() == null) {
            throw new ServerException("分页查询参数不能为空");
        }
        return ResponseEntity.ok(gateWayRouteDomain.doQuery(gateWayRoutePO));
    }

    @ApiOperation(value = "刷新路由配置信息")
    @ApiResponses({
            @ApiResponse(code = 403, message = "没有权限执行该操作；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @PostMapping(value = OauthApi.gateWayRouteRefresh, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> refresh() throws ServerException {
        gateWayRouteDomain.doRefresh();
        updateRouteProducer.doNotifyUpdateRoute();
        return ResponseEntity.ok("请求成功，稍后网关将刷新路由配置信息");
    }

}
