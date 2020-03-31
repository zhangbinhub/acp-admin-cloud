package pers.acp.admin.common.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@ApiModel("是/否响应信息")
data class BooleanInfoVo(
        @ApiModelProperty(value = "响应信息内容")
        var result: Boolean? = null
)
