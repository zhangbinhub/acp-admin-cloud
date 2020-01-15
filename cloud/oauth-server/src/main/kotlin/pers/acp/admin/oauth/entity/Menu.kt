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
 * @author zhangbin by 2018-1-17 16:59
 * @since JDK 11
 */
@Entity
@Table(name = "t_menu")
@ApiModel("菜单信息")
data class Menu(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("菜单ID")
        var id: String = "",

        @Column(length = 36, nullable = false)
        @ApiModelProperty("应用ID")
        var appId: String = "",

        @Column(nullable = false)
        @ApiModelProperty("菜单名称")
        var name: String = "",

        @ApiModelProperty("菜单图标")
        var iconType: String? = null,

        @ApiModelProperty("链接路径")
        var path: String? = null,

        @Column(nullable = false)
        @ApiModelProperty("菜单是否启用")
        var enabled: Boolean = true,

        @Column(nullable = false)
        @ApiModelProperty("是否可删除")
        var covert: Boolean = true,

        @Column(nullable = false)
        @ApiModelProperty("链接打开模式；0-内嵌，1-新标签页")
        var openType: Int = 0,

        @Column(nullable = false)
        @ApiModelProperty("序号")
        var sort: Int = 0,

        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_role_menu_set", joinColumns = [JoinColumn(name = "menuId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")])
        var roleSet: MutableSet<Role> = mutableSetOf()
) : OauthBaseTreeEntity<Menu>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val menu = other as Menu
        return EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(id, menu.id)
                .append(appId, menu.appId)
                .append(name, menu.name)
                .append(iconType, menu.iconType)
                .append(path, menu.path)
                .append(enabled, menu.enabled)
                .append(covert, menu.covert)
                .append(openType, menu.openType)
                .append(sort, menu.sort)
                .append(parentId, menu.parentId)
                .isEquals
    }

    override fun hashCode(): Int =
            HashCodeBuilder(17, 37)
                    .appendSuper(super.hashCode())
                    .append(id)
                    .append(appId)
                    .append(name)
                    .append(iconType)
                    .append(path)
                    .append(enabled)
                    .append(covert)
                    .append(openType)
                    .append(sort)
                    .append(parentId)
                    .toHashCode()

    override fun toString(): String =
            StringBuilder("Menu(")
                    .append("id=$id")
                    .append(",appId=$appId")
                    .append(",name=$name")
                    .append(",iconType=$iconType")
                    .append(",path=$path")
                    .append(",enabled=$enabled")
                    .append(",covert=$covert")
                    .append(",openType=$openType")
                    .append(",sort=$sort")
                    .append(",parentId=$parentId")
                    .append(")")
                    .toString()
}