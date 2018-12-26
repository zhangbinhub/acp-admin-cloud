package pers.acp.admin.oauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.oauth.domain.MenuDomain;
import pers.acp.admin.oauth.entity.Menu;

import java.security.Principal;
import java.util.List;

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@RestController()
@RequestMapping("/oauth")
public class MenuController {

    private final MenuDomain menuDomain;

    @Autowired
    public MenuController(MenuDomain menuDomain) {
        this.menuDomain = menuDomain;
    }

    @GetMapping("/menulist")
    public ResponseEntity<List<Menu>> userinfo(Principal user) {
        return ResponseEntity.ok(menuDomain.getMenuList(user.getName()));
    }

}
