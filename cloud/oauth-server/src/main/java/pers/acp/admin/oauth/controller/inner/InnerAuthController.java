package pers.acp.admin.oauth.controller.inner;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.path.CommonPath;
import pers.acp.admin.oauth.constant.OauthInnerApi;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@RestController
@RequestMapping(CommonPath.innerBasePath)
@Api("权限信息")
public class InnerAuthController extends BaseController {

    @ApiOperation(value = "获取当前用户权限信息", notes = "根据当前登录的用户token，返回所有授权信息")
    @GetMapping(value = OauthInnerApi.currOauth, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OAuth2Authentication> currOauth(OAuth2Authentication user) {
        return ResponseEntity.ok(user);
    }

}
