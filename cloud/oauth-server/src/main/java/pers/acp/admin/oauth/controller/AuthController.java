package pers.acp.admin.oauth.controller;

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
import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.common.constant.path.OauthApi;
import pers.acp.admin.common.permission.AuthConfigExpression;
import pers.acp.admin.oauth.domain.MenuDomain;
import pers.acp.admin.oauth.domain.ModuleFuncDomain;
import pers.acp.springboot.core.exceptions.ServerException;
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
@Api("权限信息")
public class AuthController extends BaseController {

    private final LogInstance logInstance;

    private final MenuDomain menuDomain;

    private final ModuleFuncDomain moduleFuncDomain;

    private List<String> moduleFuncCodeList = new ArrayList<>();

    @Autowired
    public AuthController(LogInstance logInstance, MenuDomain menuDomain, ModuleFuncDomain moduleFuncDomain) {
        this.logInstance = logInstance;
        this.menuDomain = menuDomain;
        this.moduleFuncDomain = moduleFuncDomain;
    }

    @PostConstruct
    public void init() {
        try {
            for (Field field : ModuleFuncCode.class.getDeclaredFields()) {
                moduleFuncCodeList.add(field.get(ModuleFuncCode.class).toString());
            }
        } catch (Exception e) {
            logInstance.error(e.getMessage(), e);
        }
    }

    @ApiOperation(value = "获取模块功能编码列表")
    @PreAuthorize(AuthConfigExpression.authConfig)
    @GetMapping(value = OauthApi.moduleFuncCodes, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<String>> getModuleFuncCode() {
        return ResponseEntity.ok(moduleFuncCodeList);
    }

}
