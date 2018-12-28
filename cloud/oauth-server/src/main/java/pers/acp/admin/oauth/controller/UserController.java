package pers.acp.admin.oauth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
