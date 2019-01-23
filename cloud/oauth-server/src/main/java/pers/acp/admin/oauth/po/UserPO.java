package pers.acp.admin.oauth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import pers.acp.admin.common.po.QueryParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangbin by 2018-1-17 15:50
 * @since JDK 11
 */
@ApiModel("用户信息参数")
public class UserPO {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginno() {
        return loginno;
    }

    public void setLoginno(String loginno) {
        this.loginno = loginno;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getLevels() {
        return levels;
    }

    public void setLevels(Integer levels) {
        this.levels = levels;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<String> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(List<String> orgIds) {
        this.orgIds = orgIds;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }

    public List<String> getOrgMngIds() {
        return orgMngIds;
    }

    public void setOrgMngIds(List<String> orgMngIds) {
        this.orgMngIds = orgMngIds;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @ApiModelProperty("用户ID")
    private String id;

    @ApiModelProperty(value = "用户名称", required = true, position = 1)
    @NotBlank(message = "用户名称不能为空")
    private String name;

    @ApiModelProperty(value = "登录账号", required = true, position = 2)
    @NotBlank(message = "登录账号不能为空")
    private String loginno;

    @ApiModelProperty(value = "手机号", required = true, position = 3)
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "用户级别", required = true, position = 4)
    @NotNull(message = "用户级别不能为空")
    private Integer levels;

    @ApiModelProperty(value = "是否启用", required = true, position = 5)
    @NotNull(message = "是否启用不能为空")
    private Boolean enabled;

    @ApiModelProperty(value = "序号", required = true, position = 6)
    private int sort = 0;

    @ApiModelProperty(value = "所属机构ID", position = 7)
    private List<String> orgIds = new ArrayList<>();

    @ApiModelProperty(value = "可管理机构ID", position = 8)
    private List<String> orgMngIds = new ArrayList<>();

    @ApiModelProperty(value = "所属角色ID", position = 9)
    private List<String> roleIds = new ArrayList<>();

    @ApiModelProperty(value = "机构名称，查询时使用", position = 10)
    private String orgName;

    @ApiModelProperty(value = "角色名称，查询时使用", position = 11)
    private String roleName;

    @ApiModelProperty(value = "分页查询参数", position = Integer.MAX_VALUE)
    private QueryParam queryParam;

}
