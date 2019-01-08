package pers.acp.admin.oauth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.oauth.domain.UserDomain;
import pers.acp.admin.oauth.entity.User;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;

import java.security.Principal;

/**
 * @author zhangbin by 11/04/2018 16:04
 * @since JDK 11
 */
@RestController()
@RequestMapping("/oauth")
@Api("用户信息")
public class UserController {

    private final UserDomain userDomain;

    @Autowired
    public UserController(UserDomain userDomain) {
        this.userDomain = userDomain;
    }

    @ApiOperation(value = "获取用户信息",
            notes = "根据当前登录的用户信息，并查询详细信息，包含用户基本信息、所属角色、所属机构")
    @ApiResponses(
            @ApiResponse(code = 400, message = "找不到用户信息", response = ErrorVO.class)
    )
    @GetMapping(value = "/userinfo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> userinfo(Principal user) throws ServerException {
        User userInfo = userDomain.findCurrUserInfo(user.getName());
        if (userInfo == null) {
            throw new ServerException("找不到用户信息");
        } else {
            return ResponseEntity.ok(userInfo);
        }
    }

    @ApiOperation(value = "更新当前用户信息",
            notes = "根据当前登录的用户信息，更新头像、名称、手机")
    @ApiResponses(
            @ApiResponse(code = 400, message = "找不到用户信息", response = ErrorVO.class)
    )
    @PatchMapping(value = "/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> updateCurrUser(@RequestBody User user) throws ServerException {
        User userInfo = userDomain.findUserById(user.getId());
        if (userInfo == null) {
            throw new ServerException("找不到用户信息");
        } else {
            userInfo.setAvatar(user.getAvatar());
            userInfo.setName(user.getName());
            userInfo.setMobile(user.getMobile());
            return ResponseEntity.ok(userDomain.doSaveUser(userInfo));
        }
    }

}
