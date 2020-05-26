package pers.acp.admin.oauth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * @author zhangbin by 2018-1-17 16:53
 * @since JDK 11
 */
@Entity
@Table(name = "t_role")
@org.hibernate.annotations.Table(appliesTo = "t_role", comment = "角色信息")
@ApiModel("角色信息")
data class Role(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("角色ID")
        var id: String = "",

        @Column(length = 36, nullable = false)
        @ApiModelProperty("应用ID")
        var appId: String = "",

        @Column(nullable = false)
        @ApiModelProperty("角色名称")
        var name: String = "",

        @Column(length = 100, nullable = false)
        @ApiModelProperty("角色编码")
        var code: String = "",

        @Column(nullable = false)
        @ApiModelProperty("角色级别")
        var levels: Int = 0,

        @Column(nullable = false)
        @ApiModelProperty("序号")
        var sort: Int = 0,

        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_user_role_set", joinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "userId", referencedColumnName = "id")])
        var userSet: MutableSet<User> = mutableSetOf(),

        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_role_menu_set", joinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "menuId", referencedColumnName = "id")])
        var menuSet: MutableSet<Menu> = mutableSetOf(),

        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_role_module_func_set", joinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "moduleId", referencedColumnName = "id")])
        var moduleFuncSet: MutableSet<ModuleFunc> = mutableSetOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val role = other as Role
        return EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(id, role.id)
                .append(appId, role.appId)
                .append(name, role.name)
                .append(code, role.code)
                .append(levels, role.levels)
                .append(sort, role.sort)
                .isEquals
    }

    override fun hashCode(): Int =
            HashCodeBuilder(17, 37)
                    .appendSuper(super.hashCode())
                    .append(id)
                    .append(appId)
                    .append(name)
                    .append(code)
                    .append(levels)
                    .append(sort)
                    .toHashCode()

    override fun toString(): String =
            StringBuilder("Role(")
                    .append("id=$id")
                    .append(",appId=$appId")
                    .append(",name=$name")
                    .append(",code=$code")
                    .append(",levels=$levels")
                    .append(",sort=$sort")
                    .append(")")
                    .toString()
}
