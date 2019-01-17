package pers.acp.admin.oauth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.path.OauthApi;
import pers.acp.admin.oauth.domain.OrganizationDomain;
import pers.acp.admin.oauth.entity.Organization;
import pers.acp.springcloud.common.log.LogInstance;

import java.util.List;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@RestController
@RequestMapping(OauthApi.basePath)
@Api("机构信息")
public class OrgController extends BaseController {

    private final LogInstance logInstance;

    private final OrganizationDomain organizationDomain;

    @Autowired
    public OrgController(LogInstance logInstance, OrganizationDomain organizationDomain) {
        this.logInstance = logInstance;
        this.organizationDomain = organizationDomain;
    }

    @ApiOperation(value = "获取机构列表", notes = "查询所有机构列表")
    @GetMapping(value = OauthApi.orgConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Organization>> orgList() {
        return ResponseEntity.ok(organizationDomain.getOrgList());
    }

}
