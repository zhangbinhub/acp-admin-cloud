package pers.acp.admin.oauth.controller.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.common.constant.path.OauthApi;
import pers.acp.admin.common.permission.RoleConfigExpression;
import pers.acp.admin.oauth.domain.RoleDomain;
import pers.acp.springcloud.common.log.LogInstance;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
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

}
