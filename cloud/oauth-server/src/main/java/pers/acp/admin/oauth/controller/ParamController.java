package pers.acp.admin.oauth.controller;

import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.permission.ParamConfigExpression;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.common.vo.RuntimeConfigVO;
import pers.acp.admin.common.constant.path.OauthApi;
import pers.acp.admin.oauth.domain.RuntimeConfigDomain;
import pers.acp.admin.oauth.entity.RuntimeConfig;
import pers.acp.admin.oauth.po.ParamPO;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@RestController
@RequestMapping(OauthApi.oauthBasePath)
@Api("运行参数配置")
public class ParamController {

    private final RuntimeConfigDomain runtimeConfigDomain;

    @Autowired
    public ParamController(RuntimeConfigDomain runtimeConfigDomain) {
        this.runtimeConfigDomain = runtimeConfigDomain;
    }

    @ApiOperation(value = "新建参数信息",
            notes = "参数名称、参数值、描述、状态")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = RuntimeConfig.class),
            @ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVO.class)
    })
    @PreAuthorize(ParamConfigExpression.paramAdd)
    @PutMapping(value = OauthApi.paramConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfig> add(@RequestBody @Valid ParamPO paramPO, BindingResult bindingResult) throws ServerException {
        if (bindingResult.hasErrors()) {
            throw new ServerException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        paramPO.setEnabled(paramPO.getEnabled() == null ? true : paramPO.getEnabled());
        return ResponseEntity.status(HttpStatus.CREATED).body(runtimeConfigDomain.doCreate(paramPO));
    }

    @ApiOperation(value = "删除指定的参数信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "id列表", required = true, paramType = "body", allowMultiple = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(ParamConfigExpression.paramDelete)
    @DeleteMapping(value = OauthApi.paramConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> delete(@RequestBody List<String> idList) {
        runtimeConfigDomain.doDelete(idList);
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "更新指定的参数信息",
            notes = "可更新参数值、描述、状态")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；配置ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(ParamConfigExpression.paramUpdate)
    @PatchMapping(value = OauthApi.paramConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfig> update(@RequestBody ParamPO paramPO) throws ServerException {
        if (CommonTools.isNullStr(paramPO.getId())) {
            throw new ServerException("配置ID不能为空");
        }
        paramPO.setEnabled(paramPO.getEnabled() == null ? true : paramPO.getEnabled());
        return ResponseEntity.ok(runtimeConfigDomain.doUpdate(paramPO));
    }

    @ApiOperation(value = "查询参数信息列表",
            notes = "查询条件：参数名称、值、状态")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(ParamConfigExpression.paramQuery)
    @PostMapping(value = OauthApi.paramConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<RuntimeConfig>> query(@RequestBody ParamPO paramPO) throws ServerException {
        if (paramPO.getQueryParam() == null) {
            throw new ServerException("分页查询参数不能为空");
        }
        return ResponseEntity.ok(runtimeConfigDomain.doQuery(paramPO));
    }

    @ApiOperation(value = "获取参数信息",
            notes = "根据参数名称获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "参数名称", required = true, paramType = "path", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "找不到参数信息；", response = ErrorVO.class)
    })
    @GetMapping(value = OauthApi.paramConfig + "/{name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfigVO> find(@PathVariable String name) throws ServerException {
        RuntimeConfig runtimeConfig = runtimeConfigDomain.findByName(name);
        if (runtimeConfig == null) {
            throw new ServerException("找不到参数信息");
        } else {
            RuntimeConfigVO runtimeConfigVO = new RuntimeConfigVO();
            BeanUtils.copyProperties(runtimeConfig, runtimeConfigVO);
            return ResponseEntity.ok(runtimeConfigVO);
        }
    }

}
