package pers.acp.admin.config.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * @author zhang by 27/02/2019
 * @since JDK 11
 */
@ApiModel("后台服务配置")
@Entity
@Table(name = "t_properties", indexes = [Index(columnList = "configApplication,configProfile,configLabel,enabled")])
class Properties {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("配置ID")
    var id: String = ""

    /**
     * 对应 spring.application.name
     */
    @Column(nullable = false)
    @ApiModelProperty("服务名")
    var configApplication: String = ""

    /**
     * 对应 spring.profiles.active
     */
    @Column(nullable = false)
    @ApiModelProperty("配置项")
    var configProfile: String = ""

    /**
     * 分支标签
     */
    @Column(nullable = false)
    @ApiModelProperty("标签")
    var configLabel: String = ""

    /**
     * 配置项键
     */
    @Column(nullable = false)
    @ApiModelProperty("键")
    var configKey: String = ""

    /**
     * 配置项值
     */
    @Column(nullable = false)
    @ApiModelProperty("值")
    var configValue: String = ""

    @ApiModelProperty("描述")
    var configDes: String? = null

    /**
     * 是否启用
     */
    @Column(nullable = false)
    @ApiModelProperty("是否启用")
    var enabled: Boolean = false

}
