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
 * @author zhangbin by 2018-1-17 16:39
 * @since JDK 11
 */
@Entity
@Table(name = "t_organization")
@ApiModel("机构信息")
public class Organization extends OauthBaseTreeEntity<Organization> {

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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public Set<User> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<User> userSet) {
        this.userSet = userSet;
    }

    public Set<User> getAdminUserSet() {
        return adminUserSet;
    }

    public void setAdminUserSet(Set<User> adminUserSet) {
        this.adminUserSet = adminUserSet;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("机构ID")
    private String id;

    @Column(nullable = false)
    @ApiModelProperty("机构名称")
    private String name;

    @Column(length = 100, nullable = false)
    @ApiModelProperty("机构编码")
    private String code = "";

    @Column(nullable = false)
    @ApiModelProperty("序号")
    private int sort;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "t_user_organization_set",
            joinColumns = {@JoinColumn(name = "organizationid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "userid", referencedColumnName = "id")})
    private Set<User> userSet = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinTable(name = "t_user_organization_mng_set",
            joinColumns = {@JoinColumn(name = "organizationid", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "userid", referencedColumnName = "id")})
    private Set<User> adminUserSet = new HashSet<>();

}
