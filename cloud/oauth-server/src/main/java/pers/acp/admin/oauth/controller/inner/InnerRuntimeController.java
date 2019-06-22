package pers.acp.admin.oauth.controller.inner;

import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.common.constant.path.CommonPath;
import pers.acp.admin.oauth.constant.OauthOpenInnerApi;
import pers.acp.admin.common.vo.RuntimeConfigVO;
import pers.acp.admin.oauth.domain.RuntimeConfigDomain;
import pers.acp.admin.oauth.entity.RuntimeConfig;
import pers.acp.spring.boot.exceptions.ServerException;
import pers.acp.spring.boot.vo.ErrorVO;

import javax.validation.constraints.NotBlank;

/**
 * @author zhang by 01/02/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(CommonPath.openInnerBasePath)
@Api("运行参数配置")
public class InnerRuntimeController {

    private final RuntimeConfigDomain runtimeConfigDomain;

    @Autowired
    public InnerRuntimeController(RuntimeConfigDomain runtimeConfigDomain) {
        this.runtimeConfigDomain = runtimeConfigDomain;
    }

    @ApiOperation(value = "获取参数信息",
            notes = "根据参数名称获取")
    @ApiResponses({
            @ApiResponse(code = 400, message = "找不到参数信息；", response = ErrorVO.class)
    })
    @GetMapping(value = OauthOpenInnerApi.runtimeConfig + "/{name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RuntimeConfigVO> find(@ApiParam(value = "参数名称", required = true) @NotBlank(message = "参数名称不能为空")
                                                @PathVariable String name) throws ServerException {
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
