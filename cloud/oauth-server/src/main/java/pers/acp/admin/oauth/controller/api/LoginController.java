package pers.acp.admin.oauth.controller.api;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.path.oauth.OauthApi;
import pers.acp.admin.common.permission.BaseExpression;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.oauth.domain.ApplicationDomain;
import pers.acp.admin.oauth.domain.UserDomain;
import pers.acp.admin.oauth.entity.Application;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.token.SecurityTokenService;
import pers.acp.admin.oauth.vo.LoginLogVO;
import pers.acp.admin.oauth.vo.OnlienInfoVO;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;
import pers.acp.springcloud.common.log.LogInstance;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
@Validated
@RestController
@RequestMapping(OauthApi.basePath)
@Api("登录信息")
public class LoginController extends BaseController {

    private final LogInstance logInstance;

    private final ApplicationDomain applicationDomain;

    private final UserDomain userDomain;

    private final SecurityTokenService securityTokenService;

    @Autowired
    public LoginController(LogInstance logInstance, ApplicationDomain applicationDomain, UserDomain userDomain, SecurityTokenService securityTokenService) {
        this.logInstance = logInstance;
        this.applicationDomain = applicationDomain;
        this.userDomain = userDomain;
        this.securityTokenService = securityTokenService;
    }

    @ApiOperation(value = "注销当前用户")
    @PostMapping(value = OauthApi.logOut, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> doLogOut(OAuth2Authentication user) throws ServerException {
        try {
            securityTokenService.removeToken(user);
            logInstance.info("用户[loginNo=" + user.getName() + "]主动下线!");
            InfoVO infoVO = new InfoVO();
            infoVO.setMessage("成功下线");
            return ResponseEntity.ok(infoVO);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    @ApiOperation(value = "获取各应用登录次数统计")
    @ApiResponses({
            @ApiResponse(code = 400, message = "没有权限做此操作；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @GetMapping(value = OauthApi.loginInfo, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<LoginLogVO>> getLoginLog() throws ServerException {
        List<LoginLogVO> loginLogVOList = new ArrayList<>();
        List<Application> applicationList = applicationDomain.getAppList();
        for (Application application : applicationList) {
            List<LoginLogVO> loginLogVOS = securityTokenService.getLoginLogList(application.getId());
            loginLogVOS.forEach(loginLogVO -> loginLogVO.setAppname(application.getAppname()));
            loginLogVOList.addAll(loginLogVOS);
        }
        return ResponseEntity.ok(loginLogVOList);
    }

    @ApiOperation(value = "获取各应用在线用户数统计")
    @GetMapping(value = OauthApi.onlineInfo, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<OnlienInfoVO>> getOnlineInfo(OAuth2Authentication user) throws ServerException {
        List<OnlienInfoVO> onlienInfoVOList = new ArrayList<>();
        List<Application> applicationList = new ArrayList<>();
        try {
            if (userDomain.isAdmin(user)) {
                applicationList = applicationDomain.getAppList();
            } else {
                Application application = applicationDomain.getApp(user.getOAuth2Request().getClientId());
                if (application != null) {
                    applicationList.add(application);
                }
            }
            for (Application application : applicationList) {
                OnlienInfoVO onlienInfoVO = new OnlienInfoVO();
                onlienInfoVO.setAppid(application.getId());
                onlienInfoVO.setAppname(application.getAppname());
                onlienInfoVO.setCount(securityTokenService.getTokensByAppId(application.getId()).size());
                onlienInfoVOList.add(onlienInfoVO);
            }
            return ResponseEntity.ok(onlienInfoVOList);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    @ApiOperation(value = "获取用户在线情况")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过，找不到用户信息；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @GetMapping(value = OauthApi.onlineInfo + "/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<OnlienInfoVO>> getOnlineInfo(@ApiParam(value = "用户id", required = true)
                                                            @PathVariable String userId) throws ServerException {
        List<OnlienInfoVO> onlienInfoVOList = new ArrayList<>();
        try {
            User user = userDomain.getUserInfo(userId);
            if (user == null) {
                throw new ServerException("找不到该用户信息");
            }
            List<Application> applicationList = applicationDomain.getAppList();
            for (Application application : applicationList) {
                OnlienInfoVO onlienInfoVO = new OnlienInfoVO();
                onlienInfoVO.setAppid(application.getId());
                onlienInfoVO.setAppname(application.getAppname());
                onlienInfoVO.setCount(securityTokenService.getTokensByAppIdAndLoginNo(application.getId(), user.getLoginno()).size());
                onlienInfoVOList.add(onlienInfoVO);
            }
            return ResponseEntity.ok(onlienInfoVOList);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

    @ApiOperation(value = "指定应用下的用户强制下线")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVO.class)
    })
    @PreAuthorize(BaseExpression.adminOnly)
    @DeleteMapping(value = OauthApi.onlineInfo + "/{appId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> delete(@ApiParam(value = "应用id", required = true)
                                         @PathVariable String appId,
                                         @ApiParam(value = "用户id列表", required = true) @NotEmpty(message = "id不能为空") @NotNull(message = "id不能为空")
                                         @RequestBody List<String> idList) throws ServerException {
        try {
            for (String id : idList) {
                User userInfo = userDomain.getUserInfo(id);
                securityTokenService.removeTokensByAppIdAndLoginNo(appId, userInfo.getLoginno());
                logInstance.info("用户[" + userInfo.getName() + "(" + userInfo.getLoginno() + ")]被管理员强制下线!");
            }
            InfoVO infoVO = new InfoVO();
            infoVO.setMessage("成功下线");
            return ResponseEntity.ok(infoVO);
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

}
