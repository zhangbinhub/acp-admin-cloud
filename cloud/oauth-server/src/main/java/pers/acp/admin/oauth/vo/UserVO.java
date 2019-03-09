package pers.acp.admin.oauth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import pers.acp.admin.common.po.QueryParam;
import pers.acp.admin.oauth.entity.Organization;
import pers.acp.admin.oauth.entity.Role;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhangbin by 2018-1-17 15:50
 * @since JDK 11
 */
@ApiModel("用户信息详情")
public class UserVO {

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

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Set<Organization> getOrganizationSet() {
        return organizationSet;
    }

    public void setOrganizationSet(Set<Organization> organizationSet) {
        this.organizationSet = organizationSet;
    }

    public Set<Organization> getOrganizationMngSet() {
        return organizationMngSet;
    }

    public void setOrganizationMngSet(Set<Organization> organizationMngSet) {
        this.organizationMngSet = organizationMngSet;
    }

    public Set<Role> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<Role> roleSet) {
        this.roleSet = roleSet;
    }

    @ApiModelProperty("用户ID")
    private String id;

    @ApiModelProperty(value = "用户名称", position = 1)
    private String name;

    @ApiModelProperty(value = "登录号", position = 2)
    private String loginno;

    @ApiModelProperty(value = "手机号", position = 3)
    private String mobile;

    @ApiModelProperty(value = "用户级别", position = 4)
    private int levels;

    @ApiModelProperty(value = "是否启用", position = 5)
    private boolean enabled;

    @ApiModelProperty(value = "序号", position = 6)
    private int sort;

    @ApiModelProperty(value = "所属机构", position = 7)
    private Set<Organization> organizationSet = new HashSet<>();

    @ApiModelProperty(value = "可管理的机构", position = 8)
    private Set<Organization> organizationMngSet = new HashSet<>();

    @ApiModelProperty(value = "所属角色", position = 9)
    private Set<Role> roleSet = new HashSet<>();

}
