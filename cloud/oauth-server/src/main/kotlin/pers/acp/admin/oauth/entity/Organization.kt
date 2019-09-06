package pers.acp.admin.oauth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import pers.acp.admin.oauth.base.OauthBaseTreeEntity

import javax.persistence.*

/**
 * @author zhangbin by 2018-1-17 16:39
 * @since JDK 11
 */
@Entity
@Table(name = "t_organization")
@ApiModel("机构信息")
class Organization : OauthBaseTreeEntity<Organization>() {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("机构ID")
    var id: String = ""

    @Column(nullable = false)
    @ApiModelProperty("机构名称")
    var name: String = ""

    @Column(length = 100)
    @ApiModelProperty("机构编码")
    var code: String = ""

    @Column(nullable = false)
    @ApiModelProperty("序号")
    var sort: Int = 0

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
    @JoinTable(name = "t_user_organization_set", joinColumns = [JoinColumn(name = "organizationId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "userId", referencedColumnName = "id")])
    var userSet: MutableSet<User> = mutableSetOf()

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
    @JoinTable(name = "t_user_organization_mng_set", joinColumns = [JoinColumn(name = "organizationId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "userId", referencedColumnName = "id")])
    var adminUserSet: MutableSet<User> = mutableSetOf()

}