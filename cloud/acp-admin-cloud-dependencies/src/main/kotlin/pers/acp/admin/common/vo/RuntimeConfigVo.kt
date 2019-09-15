package pers.acp.admin.common.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
@ApiModel("运行配置信息")
data class RuntimeConfigVo(
        @ApiModelProperty("名称")
        var name: String? = null,

        @ApiModelProperty("值")
        var value: String = "",

        @ApiModelProperty("描述")
        var configDes: String = "",

        @ApiModelProperty("是否启用")
        var enabled: Boolean = true
)
