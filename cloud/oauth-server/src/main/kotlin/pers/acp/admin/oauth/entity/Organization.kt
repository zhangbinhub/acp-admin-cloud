package pers.acp.admin.oauth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.hibernate.annotations.GenericGenerator
import pers.acp.admin.oauth.base.OauthBaseTreeEntity

import javax.persistence.*

/**
 * @author zhangbin by 2018-1-17 16:39
 * @since JDK 11
 */
@Entity
@Table(name = "t_organization", indexes = [
    Index(columnList = "area"),
    Index(columnList = "code"),
    Index(columnList = "parentId")])
@ApiModel("机构信息")
data class Organization(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("机构ID")
        var id: String = "",

        @Column(nullable = false)
        @ApiModelProperty("机构名称")
        var name: String = "",

        @Column(length = 100, nullable = false)
        @ApiModelProperty("机构区域")
        var area: String = "",

        @Column(length = 100, nullable = false)
        @ApiModelProperty("机构编码")
        var code: String = "",

        @Column(nullable = false)
        @ApiModelProperty("序号")
        var sort: Int = 0,

        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_user_organization_set", joinColumns = [JoinColumn(name = "organizationId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "userId", referencedColumnName = "id")])
        var userSet: MutableSet<User> = mutableSetOf(),

        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_user_organization_mng_set", joinColumns = [JoinColumn(name = "organizationId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "userId", referencedColumnName = "id")])
        var adminUserSet: MutableSet<User> = mutableSetOf()
) : OauthBaseTreeEntity<Organization>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val organization = other as Organization
        return EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(id, organization.id)
                .append(name, organization.name)
                .append(area, organization.area)
                .append(code, organization.code)
                .append(sort, organization.sort)
                .append(parentId, organization.parentId)
                .isEquals
    }

    override fun hashCode(): Int =
            HashCodeBuilder(17, 37)
                    .appendSuper(super.hashCode())
                    .append(id)
                    .append(name)
                    .append(area)
                    .append(code)
                    .append(sort)
                    .append(parentId)
                    .toHashCode()

    override fun toString(): String =
            StringBuilder("Organization(")
                    .append("id=$id")
                    .append(",name=$name")
                    .append(",area=$area")
                    .append(",code=$code")
                    .append(",sort=$sort")
                    .append(",parentId=$parentId")
                    .append(")")
                    .toString()
}