package pers.acp.admin.oauth.controller.api;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pers.acp.admin.common.base.BaseController;
import pers.acp.admin.common.constant.path.OauthApi;
import pers.acp.admin.common.permission.OrgConfigExpression;
import pers.acp.admin.common.vo.InfoVO;
import pers.acp.admin.oauth.domain.OrganizationDomain;
import pers.acp.admin.oauth.entity.Organization;
import pers.acp.admin.oauth.po.OrganizationPO;
import pers.acp.admin.oauth.vo.OrganizationVO;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springboot.core.vo.ErrorVO;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@RestController
@RequestMapping(OauthApi.basePath)
@Api("机构信息")
public class OrgController extends BaseController {

    private final OrganizationDomain organizationDomain;

    @Autowired
    public OrgController(OrganizationDomain organizationDomain) {
        this.organizationDomain = organizationDomain;
    }

    @ApiOperation(value = "获取机构列表", notes = "查询所有机构列表")
    @GetMapping(value = OauthApi.orgConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Organization>> orgList() {
        return ResponseEntity.ok(organizationDomain.getOrgList());
    }

    @ApiOperation(value = "新建机构信息",
            notes = "名称、编码、上级ID、序号、关联用户")
    @ApiResponses({
            @ApiResponse(code = 201, message = "创建成功", response = Organization.class),
            @ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVO.class)
    })
    @PreAuthorize(OrgConfigExpression.orgAdd)
    @PutMapping(value = OauthApi.orgConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Organization> add(OAuth2Authentication user, @RequestBody @Valid OrganizationPO organizationPO, BindingResult bindingResult) throws ServerException {
        if (bindingResult.hasErrors()) {
            throw new ServerException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationDomain.doCreate(user.getName(), organizationPO));
    }

    @ApiOperation(value = "删除指定的机构信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "idList", value = "id列表", required = true, paramType = "body", allowMultiple = true, dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；", response = ErrorVO.class)
    })
    @PreAuthorize(OrgConfigExpression.orgDelete)
    @DeleteMapping(value = OauthApi.orgConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InfoVO> delete(OAuth2Authentication user, @RequestBody List<String> idList) throws ServerException {
        organizationDomain.doDelete(user.getName(), idList);
        InfoVO infoVO = new InfoVO();
        infoVO.setMessage("删除成功");
        return ResponseEntity.ok(infoVO);
    }

    @ApiOperation(value = "更新机构信息",
            notes = "名称、编码、上级ID、序号、关联用户")
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；没有权限做此操作；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(OrgConfigExpression.orgUpdate)
    @PatchMapping(value = OauthApi.orgConfig, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Organization> update(OAuth2Authentication user, @RequestBody @Valid OrganizationPO organizationPO) throws ServerException {
        if (CommonTools.isNullStr(organizationPO.getId())) {
            throw new ServerException("ID不能为空");
        }
        return ResponseEntity.ok(organizationDomain.doUpdate(user.getName(), organizationPO));
    }

    @ApiOperation(value = "获取机构详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "机构id", required = true, paramType = "path", dataType = "String")
    })
    @ApiResponses({
            @ApiResponse(code = 400, message = "参数校验不通过；ID不能为空；找不到信息；", response = ErrorVO.class)
    })
    @PreAuthorize(OrgConfigExpression.orgConfig)
    @GetMapping(value = OauthApi.orgConfig + "/{orgId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrganizationVO> orgInfo(@PathVariable String orgId) throws ServerException {
        if (CommonTools.isNullStr(orgId)) {
            throw new ServerException("ID不能为空");
        }
        return ResponseEntity.ok(organizationDomain.getOrgInfo(orgId));
    }

}
