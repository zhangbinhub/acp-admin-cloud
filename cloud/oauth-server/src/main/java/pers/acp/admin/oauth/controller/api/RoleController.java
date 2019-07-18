package pers.acp.admin.oauth.controller.api;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.annotation.DuplicateSubmission;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.common.permission.BaseExpression;
import pers.acp.admin.oauth.constant.OauthApi;
import pers.acp.admin.oauth.constant.RoleConfigExpression;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.oauth.domain.RoleDomain;
import pers.acp.admin.oauth.entity.Role;
import pers.acp.admin.oauth.po.RolePO;
import pers.acp.admin.oauth.vo.RoleVO;
import pers.acp.core.CommonTools;
import pers.acp.spring.boot.exceptions.ServerException;
import pers.acp.spring.boot.vo.ErrorVO;
import pers.acp.spring.cloud.log.LogInstance;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api("角色信息")
public class RoleController extends BaseController {

    private final LogInstance logInstance;

    private final RoleDomain roleDomain;

    private List<String> roleCodeList = new ArrayList<>();

    @Autowired
    public RoleController(LogInstance logInstance, RoleDomain roleDomain) {
        this.logInstance = logInstance;
        this.roleDomain = roleDomain;
    }

    @PostConstruct
    public void init() {
        try {
            for (Field field : RoleCode.class.getDeclaredFields()) {
                String code = field.get(RoleCode.class).toString();
                if (!RoleCode.prefix.equals(code)) {
                    roleCodeList.add(code);
                }
            }
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
    }

    @ApiOperation(value = "获取角色编码列表")
    @PreAuthorize(RoleConfigExpression.roleConfig)
    @GetMapping(value = OauthApi.roleCodes, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<String>> getModuleFuncCode() {
        return ResponseEntity.ok(roleCodeList);
    }

    @ApiOperation(value = "获取指定应用下可编辑的角色列表", notes = "查询指定应用下可编辑的角色列表")
    @PreAuthorize(BaseExpression.Companion.sysConfig)
    @GetMapping(value = OauthApi.roleList + "/{appId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Role>> getRoleList(OAuth2Authentication user, @PathVariable String appId) throws ServerException {
        if (CommonTools.isNullStr(appId)) {
            throw new ServerException("应用ID不能为空，请重新输入");
        }
        return ResponseEntity.ok(roleDomain.getRoleListByAppId(user.getName(), appId));
    }

    @ApiOperation(value = "获取角色列表", notes = "查询所有角色列表")
    @PreAuthorize(RoleConfigExpression.roleQuery)
    @GetMapping(value = OauthApi.roleConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Role>> roleList() {
        return ResponseEntity.ok(roleDomain.getRoleList());
    }

    @ApiOperation(value = "新建角色信息",
            notes = "名称、编码、应用ID、级别、序号、关联用户、关联菜单、关联模块功能")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = Role.class),
            @ApiResponse(code = 400, message = "参数校验不通过；角色编码非法，请重新输入；", response = ErrorVO.class)
    })
    @PreAuthorize(RoleConfigExpression.roleAdd)
    @PutMapping(value = OauthApi.roleConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @DuplicateSubmission
    public ResponseEntity<Role> add(OAuth2Authentication user, @RequestBody @Valid RolePO rolePO) throws ServerException {
        if (CommonTools.isNullStr(rolePO.getAppid())) {
            throw new ServerException("应用ID不能为空");
        }
        if (!roleCodeList.contains(rolePO.getCode())) {
            throw new ServerException("角色编码非法，请重新输入");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(roleDomain.doCreate(rolePO, user.getName()));
    }

    @ApiOperation(value = "删除指定的角色信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVO.class)
    })
    @PreAuthorize(RoleConfigExpression.roleDelete)
    @DeleteMapping(value = OauthApi.roleConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> delete(OAuth2Authentication user,
                                         @ApiParam(value = "id列表", required = true) @NotEmpty(message = "id不能为空") @NotNull(message = "id不能为空")
                                         @RequestBody List<String> idList) throws ServerException {
        roleDomain.doDelete(user.getName(), idList);
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "更新角色信息",
            notes = "名称、编码、级别、序号、关联用户、关联菜单、关联模块功能")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；角色编码非法，请重新输入；没有权限做此操作；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(RoleConfigExpression.roleUpdate)
    @PatchMapping(value = OauthApi.roleConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @DuplicateSubmission
    public ResponseEntity<Role> update(OAuth2Authentication user, @RequestBody @Valid RolePO rolePO) throws ServerException {
        if (!roleCodeList.contains(rolePO.getCode())) {
            throw new ServerException("角色编码非法，请重新输入");
        }
        if (CommonTools.isNullStr(rolePO.getId())) {
            throw new ServerException("ID不能为空");
        }
        return ResponseEntity.ok(roleDomain.doUpdate(user.getName(), rolePO));
    }

    @ApiOperation(value = "获取角色详细信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(RoleConfigExpression.roleQuery)
    @GetMapping(value = OauthApi.roleConfig + "/{roleId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RoleVO> orgInfo(@ApiParam(value = "角色id", required = true) @NotBlank(message = "角色id不能为空")
                                          @PathVariable String roleId) throws ServerException {
        return ResponseEntity.ok(roleDomain.getRoleInfo(roleId));
    }

}
