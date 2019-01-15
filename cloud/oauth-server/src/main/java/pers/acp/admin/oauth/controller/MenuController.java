package pers.acp.admin.oauth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.common.constant.path.OauthApi;
import pers.acp.admin.oauth.domain.MenuDomain;
import pers.acp.admin.oauth.entity.Menu;

import java.security.Principal;
import java.util.List;

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@RestController
@RequestMapping(OauthApi.oauthBasePath)
@Api("菜单信息")
public class MenuController {

    private final MenuDomain menuDomain;

    @Autowired
    public MenuController(MenuDomain menuDomain) {
        this.menuDomain = menuDomain;
    }

    @ApiOperation(value = "获取当前用户所属菜单", notes = "根据当前登录的用户信息，查询有权访问的菜单列表")
    @GetMapping(value = OauthApi.currMenu, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Menu>> menuList(Principal user) {
        return ResponseEntity.ok(menuDomain.getMenuList(user.getName()));
    }

}
