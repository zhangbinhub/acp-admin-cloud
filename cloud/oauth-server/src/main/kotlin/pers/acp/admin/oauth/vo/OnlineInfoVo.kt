package pers.acp.admin.oauth.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
@ApiModel("在线用户数统计")
data class OnlineInfoVo(

        @ApiModelProperty(value = "应用ID", position = 1)
        var appId: String? = null,

        @ApiModelProperty(value = "应用名称", position = 2)
        var appName: String? = null,

        @ApiModelProperty(value = "有效token数", position = 3)
        var count: Long = 0

)
