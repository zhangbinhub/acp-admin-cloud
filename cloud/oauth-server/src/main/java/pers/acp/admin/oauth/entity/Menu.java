package pers.acp.admin.oauth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import pers.acp.admin.oauth.base.OauthBaseTreeEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangbin by 2018-1-17 16:59
 * @since JDK 11
 */
@Entity
@Table(name = "t_menu")
@ApiModel("菜单信息")
public class Menu extends OauthBaseTreeEntity<Menu> {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCovert() {
        return covert;
    }

    public void setCovert(boolean covert) {
        this.covert = covert;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getOpentype() {
        return opentype;
    }

    public void setOpentype(int opentype) {
        this.opentype = opentype;
    }

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
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
    @ApiModelProperty("菜单ID")
    private String id;

    @Column(length = 36, nullable = false)
    @ApiModelProperty("应用ID")
    private String appid;

    @Column(nullable = false)
    @ApiModelProperty("菜单名称")
    private String name;

    @ApiModelProperty("菜单图标")
    private String iconType;

    @ApiModelProperty("链接路径")
    private String path;

    @Column(nullable = false)
    @ApiModelProperty("菜单是否启用")
    private boolean enabled = true;

    @Column(nullable = false)
    @ApiModelProperty("是否可删除")
    private boolean covert = true;

    @Column(nullable = false)
    @ApiModelProperty("链接打开模式；0-内嵌，1-新标签页")
    private int opentype = 0;

    @Column(nullable = false)
    @ApiModelProperty("序号")
    private int sort;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "t_role_menu_set",
            joinColumns = {@JoinColumn(name = "menuid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "roleid", referencedColumnName = "id")})
    private Set<Role> roleSet = new HashSet<>();

}
