package pers.acp.admin.oauth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * @author zhangbin by 2018-1-17 15:50
 * @since JDK 11
 */
@Entity
@Table(name = "t_user")
@ApiModel("用户信息")
data class User(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("用户ID")
        var id: String = "",

        @Column(nullable = false)
        @ApiModelProperty("用户名称")
        var name: String = "",

        @Column(length = 50, unique = true, nullable = false)
        @ApiModelProperty("登录号")
        var loginNo: String = "",

        @JsonIgnore
        @Column(nullable = false)
        var password: String = "",

        @Column(length = 20, unique = true, nullable = false)
        @ApiModelProperty("手机号")
        var mobile: String = "",

        @Column(nullable = false)
        @ApiModelProperty("用户级别")
        var levels: Int = 0,

        @Column(nullable = false)
        @ApiModelProperty("是否启用")
        var enabled: Boolean = false,

        @Lob
        @ApiModelProperty("头像")
        var avatar: String? = null,

        @Column(nullable = false)
        @ApiModelProperty("序号")
        var sort: Int = 0,

        @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_user_organization_set", joinColumns = [JoinColumn(name = "userId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "organizationId", referencedColumnName = "id")])
        @ApiModelProperty("所属机构")
        var organizationSet: MutableSet<Organization> = mutableSetOf(),

        @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_user_organization_mng_set", joinColumns = [JoinColumn(name = "userId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "organizationId", referencedColumnName = "id")])
        @ApiModelProperty("可管理的机构")
        var organizationMngSet: MutableSet<Organization> = mutableSetOf(),

        @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_user_role_set", joinColumns = [JoinColumn(name = "userId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")])
        @ApiModelProperty("所属角色")
        var roleSet: MutableSet<Role> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val user = other as User
        return EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(id, user.id)
                .append(name, user.name)
                .append(loginNo, user.loginNo)
                .append(mobile, user.mobile)
                .append(levels, user.levels)
                .append(enabled, user.enabled)
                .append(sort, user.sort)
                .isEquals
    }

    override fun hashCode(): Int =
            HashCodeBuilder(17, 37)
                    .appendSuper(super.hashCode())
                    .append(id)
                    .append(name)
                    .append(loginNo)
                    .append(mobile)
                    .append(levels)
                    .append(enabled)
                    .append(sort)
                    .toHashCode()

    override fun toString(): String =
            StringBuilder("User(")
                    .append("id=$id")
                    .append(",name=$name")
                    .append(",loginNo=$loginNo")
                    .append(",mobile=$mobile")
                    .append(",levels=$levels")
                    .append(",enabled=$enabled")
                    .append(",sort=$sort")
                    .append(")")
                    .toString()
}
