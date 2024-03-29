package pers.acp.admin.common.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@ApiModel("内部响应信息")
data class InnerInfoVo(
    @ApiModelProperty(value = "是否成功")
    var success: Boolean = true,
    @ApiModelProperty(value = "响应信息内容")
    var message: String? = null
)
