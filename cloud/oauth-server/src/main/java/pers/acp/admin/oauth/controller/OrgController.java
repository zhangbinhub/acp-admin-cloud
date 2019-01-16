package pers.acp.admin.oauth.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.path.OauthApi;
import pers.acp.springcloud.common.log.LogInstance;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@RestController
@RequestMapping(OauthApi.basePath)
@Api("机构信息")
public class OrgController extends BaseController {

    private final LogInstance logInstance;

    @Autowired
    public OrgController(LogInstance logInstance) {
        this.logInstance = logInstance;
    }

}
