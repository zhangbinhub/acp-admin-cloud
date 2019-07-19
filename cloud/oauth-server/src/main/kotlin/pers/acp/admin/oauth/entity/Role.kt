package pers.acp.admin.oauth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * @author zhangbin by 2018-1-17 16:53
 * @since JDK 11
 */
@Entity
@Table(name = "t_role")
@ApiModel("角色信息")
class Role {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("角色ID")
    var id: String = ""

    @Column(length = 36, nullable = false)
    @ApiModelProperty("应用ID")
    var appId: String = ""

    @Column(nullable = false)
    @ApiModelProperty("角色名称")
    var name: String = ""

    @Column(length = 100, nullable = false)
    @ApiModelProperty("角色编码")
    var code: String = ""

    @Column(nullable = false)
    @ApiModelProperty("角色级别")
    var levels: Int = 0

    @Column(nullable = false)
    @ApiModelProperty("序号")
    var sort: Int = 0

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
    @JoinTable(name = "t_user_role_set", joinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "userId", referencedColumnName = "id")])
    var userSet: MutableSet<User> = mutableSetOf()

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
    @JoinTable(name = "t_role_menu_set", joinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "menuId", referencedColumnName = "id")])
    var menuSet: MutableSet<Menu> = mutableSetOf()

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
    @JoinTable(name = "t_role_module_func_set", joinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "moduleId", referencedColumnName = "id")])
    var moduleFuncSet: MutableSet<ModuleFunc> = mutableSetOf()

}
