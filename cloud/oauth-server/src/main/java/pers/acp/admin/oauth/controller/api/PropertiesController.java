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
import pers.acp.admin.oauth.domain.PropertiesDomain;
import pers.acp.admin.oauth.entity.Properties;
import pers.acp.admin.oauth.feign.OauthServer;
import pers.acp.admin.oauth.po.PropertiesPO;
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
@Api("服务配置信息")
public class PropertiesController extends BaseController {

    private final PropertiesDomain propertiesDomain;

    private final OauthServer oauthServer;

    @Autowired
    public PropertiesController(PropertiesDomain propertiesDomain, OauthServer oauthServer) {
        this.propertiesDomain = propertiesDomain;
        this.oauthServer = oauthServer;
    }

    @ApiOperation(value = "新建参数信息",
            notes = "服务名称、配置项、标签、键、值、描述")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = Properties.class),
            @ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @PutMapping(value = OauthApi.propertiesConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Properties> add(@RequestBody @Valid PropertiesPO propertiesPO) throws ServerException {
        Properties properties = propertiesDomain.doCreate(propertiesPO);
        return ResponseEntity.status(HttpStatus.CREATED).body(properties);
    }

    @ApiOperation(value = "删除指定的服务配置信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @DeleteMapping(value = OauthApi.propertiesConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> delete(@ApiParam(value = "id列表", required = true) @NotEmpty(message = "id不能为空") @NotNull(message = "id不能为空")
                                         @RequestBody List<String> idList) {
        propertiesDomain.doDelete(idList);
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "更新指定的参数信息",
            notes = "可更新服务名称、配置项、标签、键、值、描述")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；配置ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @PatchMapping(value = OauthApi.propertiesConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Properties> update(@RequestBody @Valid PropertiesPO propertiesPO) throws ServerException {
        if (CommonTools.isNullStr(propertiesPO.getId())) {
            throw new ServerException("配置ID不能为空");
        }
        Properties properties = propertiesDomain.doUpdate(propertiesPO);
        return ResponseEntity.ok(properties);
    }

    @ApiOperation(value = "查询参数信息列表",
            notes = "查询条件：服务名称、配置项、标签、键、状态")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @PostMapping(value = OauthApi.propertiesConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<Properties>> query(@RequestBody PropertiesPO propertiesPO) throws ServerException {
        if (propertiesPO.getQueryParam() == null) {
            throw new ServerException("分页查询参数不能为空");
        }
        return ResponseEntity.ok(propertiesDomain.doQuery(propertiesPO));
    }

    @ApiOperation(value = "刷新配置信息")
    @ApiResponses({
            @ApiResponse(code = 403, message = "没有权限执行该操作；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @PostMapping(value = OauthApi.propertiesRefresh, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> refresh() throws ServerException {
        oauthServer.busRefresh();
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("请求成功，稍后所有服务将刷新配置信息");
        return ResponseEntity.ok(infoVO);
    }

}
