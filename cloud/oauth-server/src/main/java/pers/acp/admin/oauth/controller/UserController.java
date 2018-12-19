package pers.acp.admin.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.springboot.core.exceptions.ServerException;

import java.security.Principal;

/**
 * @author zhangbin by 11/04/2018 16:04
 * @since JDK 11
 */
@RestController()
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping("/oauth/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping("/oauth/userinfo")
    public ResponseEntity<User> userinfo(Principal user) throws ServerException {
        User userInfo = userRepository.findByLoginno(user.getName()).orElse(null);
        if (userInfo == null) {
            throw new ServerException("找不到用户信息");
        } else {
            return ResponseEntity.ok(userInfo);
        }
    }

}
