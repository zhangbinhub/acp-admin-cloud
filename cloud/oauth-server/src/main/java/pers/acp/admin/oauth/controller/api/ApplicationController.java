package pers.acp.admin.oauth.controller.api;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.permission.AppConfigExpression;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.common.constant.path.oauth.OauthApi;
import pers.acp.admin.oauth.domain.ApplicationDomain;
import pers.acp.admin.oauth.entity.Application;
import pers.acp.admin.oauth.po.ApplicationPO;
import pers.acp.admin.oauth.producer.instance.UpdateConfigProducer;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
@RestController
@RequestMapping(OauthApi.basePath)
@Api("应用信息")
public class ApplicationController extends BaseController {

    private final ApplicationDomain applicationDomain;

    private final UpdateConfigProducer updateConfigProducer;

    @Autowired
    public ApplicationController(ApplicationDomain applicationDomain, UpdateConfigProducer updateConfigProducer) {
        this.applicationDomain = applicationDomain;
        this.updateConfigProducer = updateConfigProducer;
    }

    @ApiOperation(value = "新建应用信息",
            notes = "应用名称、token 有效期、refresh token 有效期")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = Application.class),
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(AppConfigExpression.appAdd)
    @PutMapping(value = OauthApi.appConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Application> add(@RequestBody @Valid ApplicationPO applicationPO, BindingResult bindingResult) throws ServerException {
        if (bindingResult.hasErrors()) {
            throw new ServerException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        Application application = applicationDomain.doCreate(applicationPO);
        updateConfigProducer.doNotifyUpdateApp();
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    @ApiOperation(value = "删除指定的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "id列表", required = true, paramType = "body", allowMultiple = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(AppConfigExpression.appDelete)
    @DeleteMapping(value = OauthApi.appConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> delete(@RequestBody List<String> idList) {
        applicationDomain.doDelete(idList);
        updateConfigProducer.doNotifyUpdateApp();
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "更新指定的信息",
            notes = "可更新应用名称、token 有效期、refresh token 有效期")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(AppConfigExpression.appUpdate)
    @PatchMapping(value = OauthApi.appConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Application> update(@RequestBody ApplicationPO applicationPO) throws ServerException {
        if (CommonTools.isNullStr(applicationPO.getId())) {
            throw new ServerException("ID不能为空");
        }
        Application application = applicationDomain.doUpdate(applicationPO);
        updateConfigProducer.doNotifyUpdateApp();
        return ResponseEntity.ok(application);
    }

    @ApiOperation(value = "查询信息列表",
            notes = "查询条件：应用名称")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(AppConfigExpression.appQuery)
    @PostMapping(value = OauthApi.appConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<Application>> query(@RequestBody ApplicationPO applicationPO) throws ServerException {
        if (applicationPO.getQueryParam() == null) {
            throw new ServerException("分页查询参数不能为空");
        }
        return ResponseEntity.ok(applicationDomain.doQuery(applicationPO));
    }

    @ApiOperation(value = "获取应用列表", notes = "查询所有应用列表")
    @GetMapping(value = OauthApi.appConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Application>> appList() {
        return ResponseEntity.ok(applicationDomain.getAppList());
    }

    @ApiOperation(value = "更新应用密钥")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "应用id", required = true, paramType = "path", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(AppConfigExpression.appUpdateSecret)
    @GetMapping(value = OauthApi.updateSecret + "/{appId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Application> updateSecret(@PathVariable String appId) throws ServerException {
        if (CommonTools.isNullStr(appId)) {
            throw new ServerException("ID不能为空");
        }
        Application application = applicationDomain.doUpdateSecret(appId);
        updateConfigProducer.doNotifyUpdateApp();
        return ResponseEntity.ok(application);
    }

}
