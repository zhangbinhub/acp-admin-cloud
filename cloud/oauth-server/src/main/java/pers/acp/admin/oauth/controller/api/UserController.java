package pers.acp.admin.oauth.controller.api;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.path.oauth.OauthApi;
import pers.acp.admin.common.permission.UserConfigExpression;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.oauth.domain.UserDomain;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.po.UserInfoPO;
import pers.acp.admin.oauth.po.UserPO;
import pers.acp.admin.oauth.vo.UserVO;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangbin by 11/04/2018 16:04
 * @since JDK 11
 */
@RestController
@RequestMapping(OauthApi.basePath)
@Api("用户信息")
public class UserController extends BaseController {

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
    public ResponseEntity<User> userinfo(OAuth2Authentication user) throws ServerException {
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
    public ResponseEntity<User> updateCurrUser(OAuth2Authentication user, @RequestBody @Valid UserInfoPO userInfoPO, BindingResult bindingResult) throws ServerException {
        if (bindingResult.hasErrors()) {
            throw new ServerException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        User userInfo = userDomain.findCurrUserInfo(user.getName());
        if (userInfo == null) {
            throw new ServerException("找不到用户信息");
        } else {
            if (!CommonTools.isNullStr(userInfoPO.getMobile())) {
                User otherUser = userDomain.getMobileForOtherUser(userInfoPO.getMobile(), userInfo.getId());
                if (otherUser != null) {
                    throw new ServerException("手机号已存在，请重新输入");
                }
            }
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

    @ApiOperation(value = "获取可管理的用户信息列表",
            notes = "根据当前登录的用户信息，获取可管理的用户信息列表")
    @ApiResponses({
            @ApiResponse(code = 400, message = "找不到用户信息", response = ErrorVO.class)
    })
    @PreAuthorize(UserConfigExpression.sysConfig)
    @GetMapping(value = OauthApi.modifiableUser, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<User>> modifiableUser(OAuth2Authentication user) {
        return ResponseEntity.ok(userDomain.findModifiableUserList(user.getName()));
    }

    @ApiOperation(value = "新建用户信息",
            notes = "名称、登录账号、手机号、级别、序号、是否启用、关联机构、管理机构、关联角色")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = User.class),
            @ApiResponse(code = 400, message = "参数校验不通过；角色编码非法，请重新输入；", response = ErrorVO.class)
    })
    @PreAuthorize(UserConfigExpression.userAdd)
    @PutMapping(value = OauthApi.userConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> add(OAuth2Authentication user, @RequestBody @Valid UserPO userPO, BindingResult bindingResult) throws ServerException {
        if (bindingResult.hasErrors()) {
            throw new ServerException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(userDomain.doCreate(user.getName(), userPO));
    }

    @ApiOperation(value = "删除指定的用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "id列表", required = true, paramType = "body", allowMultiple = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVO.class)
    })
    @PreAuthorize(UserConfigExpression.userDelete)
    @DeleteMapping(value = OauthApi.userConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> delete(OAuth2Authentication user, @RequestBody List<String> idList) throws ServerException {
        userDomain.doDelete(user.getName(), idList);
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "更新用户信息",
            notes = "名称、手机号、级别、序号、是否启用、关联机构、管理机构、关联角色")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；角色编码非法，请重新输入；没有权限做此操作；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(UserConfigExpression.userUpdate)
    @PatchMapping(value = OauthApi.userConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> update(OAuth2Authentication user, @RequestBody @Valid UserPO userPO, BindingResult bindingResult) throws ServerException {
        if (bindingResult.hasErrors()) {
            throw new ServerException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        if (CommonTools.isNullStr(userPO.getId())) {
            throw new ServerException("ID不能为空");
        }
        return ResponseEntity.ok(userDomain.doUpdate(user.getName(), userPO));
    }

    @ApiOperation(value = "重置用户密码",
            notes = "根据用户ID查询详细信息并重置密码")
    @ApiResponses({
            @ApiResponse(code = 400, message = "找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(UserConfigExpression.userUpdate)
    @GetMapping(value = OauthApi.userResetPwd + "/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> resetPwd(OAuth2Authentication user, @PathVariable String userId) throws ServerException {
        if (CommonTools.isNullStr(userId)) {
            throw new ServerException("ID不能为空");
        }
        userDomain.doUpdatePwd(user.getName(), userId);
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("操作成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "查询用户列表",
            notes = "查询条件：名称、登录帐号、状态、所属机构")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；", response = ErrorVO.class)
    })
    @PreAuthorize(UserConfigExpression.userQuery)
    @PostMapping(value = OauthApi.userConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<UserVO>> query(@RequestBody UserPO userPO) throws ServerException {
        if (userPO.getQueryParam() == null) {
            throw new ServerException("分页查询参数不能为空");
        }
        return ResponseEntity.ok(userDomain.doQuery(userPO));
    }

    @ApiOperation(value = "查询用户列表",
            notes = "根据用户ID查询详细信息")
    @ApiResponses({
            @ApiResponse(code = 400, message = "找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(UserConfigExpression.userQuery)
    @GetMapping(value = OauthApi.userConfig + "/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> getUserInfo(@PathVariable String userId) throws ServerException {
        if (CommonTools.isNullStr(userId)) {
            throw new ServerException("ID不能为空");
        }
        User userInfo = userDomain.getUserInfo(userId);
        if (userInfo == null) {
            throw new ServerException("找不到用户信息");
        } else {
            return ResponseEntity.ok(userInfo);
        }
    }

}
