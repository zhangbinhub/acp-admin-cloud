package pers.acp.admin.oauth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zhangbin by 2018-1-17 16:39
 * @since JDK 11
 */
@Entity
@Table(name = "t_organization")
@ApiModel("机构信息")
public class Organization {

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<Organization> getChildren() {
        return children;
    }

    public void setChildren(List<Organization> children) {
        this.children = children;
    }

    public Set<User> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<User> userSet) {
        this.userSet = userSet;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("机构ID")
    private String id;

    @Column(nullable = false)
    @ApiModelProperty("机构名称")
    private String name;

    @Column(length = 100, nullable = false)
    @ApiModelProperty("机构编码")
    private String code;

    @Column(length = 36, nullable = false)
    @ApiModelProperty("上级机构ID")
    private String parentid = "";

    @Column(nullable = false)
    @ApiModelProperty("序号")
    private int sort;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "t_user_organization_set",
            joinColumns = {@JoinColumn(name = "organizationid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "userid", referencedColumnName = "id")})
    @ApiModelProperty("关联用户")
    private Set<User> userSet = new HashSet<>();

    @Transient
    @ApiModelProperty("子机构列表")
    private List<Organization> children = new ArrayList<>();

}
