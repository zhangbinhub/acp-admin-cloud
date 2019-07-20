package pers.acp.admin.oauth.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import pers.acp.admin.oauth.base.OauthBaseTreeEntity

import javax.persistence.*

/**
 * @author zhangbin by 2018-1-17 17:10
 * @since JDK 11
 */
@Entity
@Table(name = "t_module_func", indexes = [Index(columnList = "code,appId")])
@ApiModel("模块功能信息")
class ModuleFunc : OauthBaseTreeEntity<ModuleFunc>() {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("ID")
    var id: String = ""

    @Column(length = 36, nullable = false)
    @ApiModelProperty("应用ID")
    var appId: String = ""

    @Column(nullable = false)
    @ApiModelProperty("模块名称")
    var name: String = ""

    @Column(length = 100, nullable = false)
    @ApiModelProperty("模块编码")
    var code: String = ""

    @Column(nullable = false)
    @ApiModelProperty("是否可删除")
    var covert: Boolean = true

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
    @JoinTable(name = "t_role_module_func_set", joinColumns = [JoinColumn(name = "moduleId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")])
    var roleSet: MutableSet<Role> = mutableSetOf()

}
