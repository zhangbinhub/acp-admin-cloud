package pers.acp.admin.config.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.po.QueryParam

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * @author zhang by 01/03/2019
 * @since JDK 11
 */
@ApiModel("后台服务配置参数")
data class PropertiesPo(

        @ApiModelProperty("配置ID")
        var id: String? = null,

        /**
         * 对应 spring.application.name
         */
        @ApiModelProperty(value = "服务名", position = 1)
        @get:NotBlank(message = "服务名不能为空")
        var configApplication: String? = null,

        /**
         * 对应 spring.profiles.active
         */
        @ApiModelProperty(value = "配置项", position = 2)
        @get:NotBlank(message = "配置项不能为空")
        var configProfile: String? = null,

        /**
         * 分支标签
         */
        @ApiModelProperty(value = "标签", position = 3)
        @get:NotBlank(message = "标签不能为空")
        var configLabel: String? = null,

        /**
         * 配置项键
         */
        @ApiModelProperty(value = "键", position = 4)
        @get:NotBlank(message = "键不能为空")
        var configKey: String? = null,

        /**
         * 配置项值
         */
        @ApiModelProperty(value = "值", position = 5)
        @get:NotBlank(message = "值不能为空")
        var configValue: String? = null,

        @ApiModelProperty(value = "描述", position = 6)
        var configDes: String = "",

        /**
         * 是否启用
         */
        @ApiModelProperty(value = "是否启用", position = 7)
        @get:NotNull(message = "是否启用不能为空")
        var enabled: Boolean? = null,


        @ApiModelProperty(value = "分页查询参数", position = Int.MAX_VALUE)
        var queryParam: QueryParam? = null
)
