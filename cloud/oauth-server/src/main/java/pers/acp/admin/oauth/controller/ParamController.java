package pers.acp.admin.oauth.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.permission.ParamConfigExpression;
import pers.acp.admin.oauth.constant.ApiPrefix;
import pers.acp.admin.oauth.domain.ParamDomain;
import pers.acp.admin.oauth.entity.RuntimeConfig;
import pers.acp.admin.oauth.po.ParamPO;
import pers.acp.springboot.core.vo.ErrorVO;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@RestController
@RequestMapping(ApiPrefix.basePath)
@Api("运行参数配置")
public class ParamController {

    private final ParamDomain paramDomain;

    @Autowired
    public ParamController(ParamDomain paramDomain) {
        this.paramDomain = paramDomain;
    }

    @ApiOperation(value = "新建参数信息",
            notes = "参数名称、参数值、描述、状态")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = RuntimeConfig.class),
            @ApiResponse(code = 400, message = "参数校验不通过；参数信息已存在；", response = ErrorVO.class)
    })
    @PreAuthorize(ParamConfigExpression.paramAdd)
    @PutMapping(value = ApiPrefix.paramConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfig> add(@RequestBody @Valid ParamPO paramPO) {
        // todo
        return null;
    }

    @ApiOperation(value = "删除指定的参数信息",
            notes = "可更新参数值、描述、状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paramIdList", value = "参数id", required = true, paramType = "body", allowMultiple = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(ParamConfigExpression.paramDelete)
    @DeleteMapping(value = ApiPrefix.paramConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfig> delete(@RequestBody List<String> paramIdList) {
        // todo
        return null;
    }

    @ApiOperation(value = "更新指定的参数信息",
            notes = "可更新参数值、描述、状态")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；找不到参数信息；", response = ErrorVO.class)
    })
    @PreAuthorize(ParamConfigExpression.paramUpdate)
    @PatchMapping(value = ApiPrefix.paramConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfig> update(@RequestBody @Valid ParamPO paramPO) {
        //todo
        return null;
    }

    @ApiOperation(value = "查询参数信息列表",
            notes = "查询条件：参数名称、值、状态")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(ParamConfigExpression.paramQuery)
    @PostMapping(value = ApiPrefix.paramConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<RuntimeConfig>> query(@RequestBody ParamPO paramPO) {
        // todo
        return null;
    }

    @ApiOperation(value = "获取参数信息",
            notes = "根据参数名称获取")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "参数名称", required = true, paramType = "path", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "找不到参数信息；", response = ErrorVO.class)
    })
    @GetMapping(value = ApiPrefix.paramConfig + "/{name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfig> find(@PathVariable String name) {
        // todo
        return null;
    }

}
