package pers.acp.admin.oauth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.oauth.domain.UserDomain;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.po.UserParam;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;

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
    public ResponseEntity<User> updateCurrUser(@RequestBody @Valid UserParam userParam, BindingResult bindingResult) throws ServerException {
        if (bindingResult.hasErrors()) {
            throw new ServerException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        User userInfo = userDomain.findUserById(userParam.getId());
        if (userInfo == null) {
            throw new ServerException("找不到用户信息");
        } else {
            userInfo.setAvatar(userParam.getAvatar());
            userInfo.setName(userParam.getName());
            userInfo.setMobile(userParam.getMobile());
            return ResponseEntity.ok(userDomain.doSaveUser(userInfo));
        }
    }

}
