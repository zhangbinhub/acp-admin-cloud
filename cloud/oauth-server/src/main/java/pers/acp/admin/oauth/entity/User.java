package pers.acp.admin.oauth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangbin by 2018-1-17 15:50
 * @since JDK 11
 */
@Entity
@Table(name = "t_user")
@ApiModel("用户信息")
public class User {

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public Set<Role> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<Role> roleSet) {
        this.roleSet = roleSet;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("用户ID")
    private String id;

    @Column(nullable = false)
    @ApiModelProperty("用户名称")
    private String name;

    @Column(length = 50, unique = true, nullable = false)
    @ApiModelProperty("登录号")
    private String loginno;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(length = 20, unique = true, nullable = false)
    @ApiModelProperty("手机号")
    private String mobile;

    @Column(nullable = false)
    @ApiModelProperty("用户级别")
    private int levels;

    @Column(nullable = false)
    @ApiModelProperty("是否启用")
    private boolean enabled;

    @Column(columnDefinition = "text")
    @ApiModelProperty("头像")
    private String avatar;

    @Column(nullable = false)
    @ApiModelProperty("序号")
    private int sort;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "t_user_organization_set",
            joinColumns = {@JoinColumn(name = "userid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "organizationid", referencedColumnName = "id")})
    @ApiModelProperty("所属机构")
    private Set<Organization> organizationSet = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "t_user_role_set",
            joinColumns = {@JoinColumn(name = "userid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "roleid", referencedColumnName = "id")})
    @ApiModelProperty("归属角色")
    private Set<Role> roleSet = new HashSet<>();

}
