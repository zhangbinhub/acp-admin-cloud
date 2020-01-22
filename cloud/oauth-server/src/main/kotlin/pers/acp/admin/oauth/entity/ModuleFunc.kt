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
 * @author zhangbin by 2018-1-17 17:10
 * @since JDK 11
 */
@Entity
@Table(name = "t_module_func", indexes = [Index(columnList = "code,appId")])
@ApiModel("模块功能信息")
data class ModuleFunc(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("ID")
        var id: String = "",

        @Column(length = 36, nullable = false)
        @ApiModelProperty("应用ID")
        var appId: String = "",

        @Column(nullable = false)
        @ApiModelProperty("模块名称")
        var name: String = "",

        @Column(length = 100, nullable = false)
        @ApiModelProperty("模块编码")
        var code: String = "",

        @Column(nullable = false)
        @ApiModelProperty("是否可删除")
        var covert: Boolean = true,

        @JsonIgnore
        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REFRESH])
        @JoinTable(name = "t_role_module_func_set", joinColumns = [JoinColumn(name = "moduleId", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "roleId", referencedColumnName = "id")])
        var roleSet: MutableSet<Role> = mutableSetOf()
) : OauthBaseTreeEntity<ModuleFunc>() {
    override fun equals(other: Any?): Boolean {
        if (other == null || javaClass != other.javaClass) return false
        if (this === other) return true
        val moduleFunc = other as ModuleFunc
        return EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(id, moduleFunc.id)
                .append(appId, moduleFunc.appId)
                .append(name, moduleFunc.name)
                .append(code, moduleFunc.code)
                .append(covert, moduleFunc.covert)
                .append(parentId, moduleFunc.parentId)
                .isEquals
    }

    override fun hashCode(): Int =
            HashCodeBuilder(17, 37)
                    .appendSuper(super.hashCode())
                    .append(id)
                    .append(appId)
                    .append(name)
                    .append(code)
                    .append(covert)
                    .append(parentId)
                    .toHashCode()

    override fun toString(): String =
            StringBuilder("ModuleFunc(")
                    .append("id=$id")
                    .append(",appId=$appId")
                    .append(",name=$name")
                    .append(",code=$code")
                    .append(",covert=$covert")
                    .append(",parentId=$parentId")
                    .append(")")
                    .toString()
}
