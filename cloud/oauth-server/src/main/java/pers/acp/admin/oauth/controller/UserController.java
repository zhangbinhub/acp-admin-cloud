package pers.acp.admin.oauth.controller;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.constant.path.OauthApi;
import pers.acp.admin.oauth.domain.UserDomain;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.po.UserInfoPO;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;

/**
 * @author zhangbin by 11/04/2018 16:04
 * @since JDK 11
 */
@RestController
@RequestMapping(OauthApi.oauthBasePath)
@Api("用户信息")
public class UserController {

    private final UserDomain userDomain;

    @Autowired
    public UserController(UserDomain userDomain) {
        this.userDomain = userDomain;
    }

    @ApiOperation(value = "获取当前用户信息",
            notes = "根据当前登录的用户信息，并查询详细信息，包含用户基本信息、所属角色、所属机构")
    @ApiResponses({
            @ApiResponse(code = 400, message = "找不到用户信息", response = ErrorVO.class)
    })
    @GetMapping(value = OauthApi.currUser, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> userinfo(Principal user) throws ServerException {
        User userInfo = userDomain.findCurrUserInfo(user.getName());
        if (userInfo == null) {
            throw new ServerException("找不到用户信息");
        } else {
            return ResponseEntity.ok(userInfo);
        }
    }

    @ApiOperation(value = "更新当前用户信息",
            notes = "1、根据当前登录的用户信息，更新头像、名称、手机；2、如果原密码和新密码均不为空，校验原密码并修改为新密码")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；找不到用户信息；原密码不正确；新密码为空；", response = ErrorVO.class)
    })
    @RequestMapping(value = OauthApi.currUser, method = {RequestMethod.PUT, RequestMethod.PATCH}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> updateCurrUser(Principal user, @RequestBody @Valid UserInfoPO userInfoPO, BindingResult bindingResult) throws ServerException {
        if (bindingResult.hasErrors()) {
            throw new ServerException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        User userInfo = userDomain.findCurrUserInfo(user.getName());
        if (userInfo == null) {
            throw new ServerException("找不到用户信息");
        } else {
            userInfo.setAvatar(Objects.requireNonNullElse(userInfoPO.getAvatar(), ""));
            userInfo.setName(Objects.requireNonNullElse(userInfoPO.getName(), userInfo.getName()));
            userInfo.setMobile(Objects.requireNonNullElse(userInfoPO.getMobile(), userInfo.getMobile()));
            if (!CommonTools.isNullStr(userInfoPO.getOldPassword())) {
                if (CommonTools.isNullStr(userInfoPO.getPassword())) {
                    throw new ServerException("新密码为空");
                }
                if (userInfo.getPassword().equalsIgnoreCase(userInfoPO.getOldPassword())) {
                    userInfo.setPassword(userInfoPO.getPassword());
                } else {
                    throw new ServerException("原密码不正确");
                }
            }
            return ResponseEntity.ok(userDomain.doSaveUser(userInfo));
        }
    }

}
