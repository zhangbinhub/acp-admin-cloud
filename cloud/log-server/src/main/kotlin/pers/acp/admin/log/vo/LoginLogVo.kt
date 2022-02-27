package pers.acp.admin.log.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 08/03/2019
 * @since JDK 11
 */
@ApiModel("登录次数统计")
data class LoginLogVo(

    @ApiModelProperty(value = "应用ID", position = 1)
    var appId: String? = null,

    @ApiModelProperty(value = "应用名称", position = 2)
    var appName: String? = null,

    @ApiModelProperty(value = "日期", position = 3)
    var date: String? = null,

    @ApiModelProperty(value = "获取token的次数", position = 4)
    var count: Long = 0

)
