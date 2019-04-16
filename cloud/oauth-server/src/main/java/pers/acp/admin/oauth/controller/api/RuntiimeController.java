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
import pers.acp.admin.oauth.constant.RuntimeConfigExpression;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.common.vo.RuntimeConfigVO;
import pers.acp.admin.oauth.constant.OauthApi;
import pers.acp.admin.oauth.bus.publish.RefreshEventPublish;
import pers.acp.admin.oauth.controller.inner.InnerRuntimeController;
import pers.acp.admin.oauth.domain.RuntimeConfigDomain;
import pers.acp.admin.oauth.entity.RuntimeConfig;
import pers.acp.admin.oauth.po.RuntimePO;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api("运行参数配置")
public class RuntiimeController extends BaseController {

    private final InnerRuntimeController innerRuntimeController;

    private final RuntimeConfigDomain runtimeConfigDomain;

    private final RefreshEventPublish refreshEventPublish;

    @Autowired
    public RuntiimeController(InnerRuntimeController innerRuntimeController, RuntimeConfigDomain runtimeConfigDomain, RefreshEventPublish refreshEventPublish) {
        this.innerRuntimeController = innerRuntimeController;
        this.runtimeConfigDomain = runtimeConfigDomain;
        this.refreshEventPublish = refreshEventPublish;
    }

    @ApiOperation(value = "新建参数信息",
            notes = "参数名称、参数值、描述、状态")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = RuntimeConfig.class),
            @ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVO.class)
    })
    @PreAuthorize(RuntimeConfigExpression.runtimeAdd)
    @PutMapping(value = OauthApi.runtimeConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfig> add(@RequestBody @Valid RuntimePO runtimePO) throws ServerException {
        RuntimeConfig runtimeConfig = runtimeConfigDomain.doCreate(runtimePO);
        refreshEventPublish.doNotifyUpdateRuntime();
        return ResponseEntity.status(HttpStatus.CREATED).body(runtimeConfig);
    }

    @ApiOperation(value = "删除指定的参数信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(RuntimeConfigExpression.runtimeDelete)
    @DeleteMapping(value = OauthApi.runtimeConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> delete(@ApiParam(value = "id列表", required = true) @NotEmpty(message = "id不能为空") @NotNull(message = "id不能为空")
                                         @RequestBody List<String> idList) {
        runtimeConfigDomain.doDelete(idList);
        refreshEventPublish.doNotifyUpdateRuntime();
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "更新指定的参数信息",
            notes = "可更新参数值、描述、状态")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；配置ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(RuntimeConfigExpression.runtimeUpdate)
    @PatchMapping(value = OauthApi.runtimeConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfig> update(@RequestBody @Valid RuntimePO runtimePO) throws ServerException {
        if (CommonTools.isNullStr(runtimePO.getId())) {
            throw new ServerException("配置ID不能为空");
        }
        RuntimeConfig runtimeConfig = runtimeConfigDomain.doUpdate(runtimePO);
        refreshEventPublish.doNotifyUpdateRuntime();
        return ResponseEntity.ok(runtimeConfig);
    }

    @ApiOperation(value = "查询参数信息列表",
            notes = "查询条件：参数名称、值、状态")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(RuntimeConfigExpression.runtimeQuery)
    @PostMapping(value = OauthApi.runtimeConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<RuntimeConfig>> query(@RequestBody RuntimePO runtimePO) throws ServerException {
        if (runtimePO.getQueryParam() == null) {
            throw new ServerException("分页查询参数不能为空");
        }
        return ResponseEntity.ok(runtimeConfigDomain.doQuery(runtimePO));
    }

    @ApiOperation(value = "获取参数信息",
            notes = "根据参数名称获取")
    @ApiResponses({
            @ApiResponse(code = 400, message = "找不到参数信息；", response = ErrorVO.class)
    })
    @GetMapping(value = OauthApi.runtimeConfig + "/{name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfigVO> find(@ApiParam(value = "参数名称", required = true) @NotBlank(message = "参数名称不能为空")
                                                @PathVariable String name) throws ServerException {
        return innerRuntimeController.find(name);
    }

}
