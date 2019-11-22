package pers.acp.admin.oauth.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * @author zhangbin by 2018-1-16 23:00
 * @since JDK 11
 */
@Entity
@Table(name = "t_runtimeconfig", indexes = [Index(columnList = "name,enabled")])
@ApiModel("运行配置")
data class RuntimeConfig(
        @Id
        @GenericGenerator(name = "idGenerator", strategy = "uuid")
        @GeneratedValue(generator = "idGenerator")
        @Column(length = 36, nullable = false)
        @ApiModelProperty("配置ID")
        var id: String = "",

        @Column(length = 100, unique = true, nullable = false)
        @ApiModelProperty("名称")
        var name: String = "",

        @ApiModelProperty("值")
        var value: String? = null,

        @ApiModelProperty("描述")
        var configDes: String? = null,

        @Column(nullable = false)
        @ApiModelProperty("是否启用")
        var enabled: Boolean = true,

        @Column(nullable = false)
        @ApiModelProperty("是否可删除")
        var covert: Boolean = true
)