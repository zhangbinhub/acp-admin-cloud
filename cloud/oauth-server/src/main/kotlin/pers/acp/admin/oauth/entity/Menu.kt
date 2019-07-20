package pers.acp.admin.oauth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
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
class Menu : OauthBaseTreeEntity<Menu>() {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("菜单ID")
    var id: String = ""

    @Column(length = 36, nullable = false)
    @ApiModelProperty("应用ID")
    var appId: String = ""

    @Column(nullable = false)
    @ApiModelProperty("菜单名称")
    var name: String = ""

    @ApiModelProperty("菜单图标")
    var iconType: String? = null

    @ApiModelProperty("链接路径")
    var path: String? = null

    @Column(nullable = false)
    @ApiModelProperty("菜单是否启用")
    var enabled: Boolean = true

    @Column(nullable = false)
    @ApiModelProperty("是否可删除")
    var covert: Boolean = true

    @Column(nullable = false)
    @ApiModelProperty("链接打开模式；0-内嵌，1-新标签页")
    var openType: Int = 0

    @Column(nullable = false)
    @ApiModelProperty("序号")
    var sort: Int = 0

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
    @JoinTable(name = "t_role_menu_set", joinColumns = [JoinColumn(name = "menuId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")])
    var roleSet: MutableSet<Role> = mutableSetOf()

}