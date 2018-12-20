package pers.acp.admin.oauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.oauth.domain.UserService;
import pers.acp.admin.oauth.entity.User;
import pers.acp.springboot.core.exceptions.ServerException;

import java.security.Principal;

/**
 * @author zhangbin by 11/04/2018 16:04
 * @since JDK 11
 */
@RestController()
@RequestMapping("/oauth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @GetMapping("/userinfo")
    public ResponseEntity<User> userinfo(Principal user) throws ServerException {
        User userInfo = userService.findCurrUserInfo(user.getName());
        if (userInfo == null) {
            throw new ServerException("找不到用户信息");
        } else {
            return ResponseEntity.ok(userInfo);
        }
    }

}
