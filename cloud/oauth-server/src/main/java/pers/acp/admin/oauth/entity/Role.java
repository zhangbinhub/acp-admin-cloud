package pers.acp.admin.oauth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangbin by 2018-1-17 16:53
 * @since JDK 11
 */
@Entity
@Table(name = "t_role")
@ApiModel("角色信息")
public class Role {

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Set<Menu> getMenuSet() {
        return menuSet;
    }

    public void setMenuSet(Set<Menu> menuSet) {
        this.menuSet = menuSet;
    }

    public Set<Module> getModuleSet() {
        return moduleSet;
    }

    public void setModuleSet(Set<Module> moduleSet) {
        this.moduleSet = moduleSet;
    }

    public Set<ModuleFunc> getModuleFuncSet() {
        return moduleFuncSet;
    }

    public void setModuleFuncSet(Set<ModuleFunc> moduleFuncSet) {
        this.moduleFuncSet = moduleFuncSet;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("角色ID")
    private String id;

    @Column(length = 36, nullable = false)
    @ApiModelProperty("应用ID")
    private String appid;

    @Column(nullable = false)
    @ApiModelProperty("角色名称")
    private String name;

    @Column(length = 100, nullable = false)
    @ApiModelProperty("角色编码")
    private String code;

    @Column(nullable = false)
    @ApiModelProperty("角色级别")
    private int levels;

    @Column(nullable = false)
    @ApiModelProperty("序号")
    private int sort;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "t_role_menu_set",
            joinColumns = {@JoinColumn(name = "roleid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "menuid", referencedColumnName = "id")})
    private Set<Menu> menuSet = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "t_role_module_set",
            joinColumns = {@JoinColumn(name = "roleid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "moduleid", referencedColumnName = "id")})
    private Set<Module> moduleSet = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "t_role_module_func_set",
            joinColumns = {@JoinColumn(name = "roleid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "funcid", referencedColumnName = "id")})
    private Set<ModuleFunc> moduleFuncSet = new HashSet<>();

}
