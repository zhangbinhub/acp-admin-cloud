package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 08/01/2019
 * @since JDK 11
 */
@ApiModel("更新当前用户信息参数")
data class UserInfoPo(

        @ApiModelProperty(value = "名称", required = true)
        @get:NotBlank(message = "名称不能为空")
        var name: String? = null,

        @ApiModelProperty(value = "手机号", required = true, position = 1)
        @get:NotBlank(message = "手机号不能为空")
        var mobile: String? = null,

        @ApiModelProperty(value = "头像图片base64数据", position = 2)
        var avatar: String? = null,

        @ApiModelProperty(value = "原密码", position = 3)
        var oldPassword: String? = null,

        @ApiModelProperty(value = "新密码", position = 4)
        var password: String? = null

)
