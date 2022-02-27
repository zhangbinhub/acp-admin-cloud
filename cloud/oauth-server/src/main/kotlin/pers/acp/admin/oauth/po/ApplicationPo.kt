package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
@ApiModel("应用配置参数")
data class ApplicationPo(
    @ApiModelProperty("应用ID")
    var id: String? = null,

    @ApiModelProperty(value = "应用名称", required = true, position = 1)
    @get:NotBlank(message = "应用名称不能为空")
    var appName: String? = null,

    @ApiModelProperty("权限范围，多个权限“,”分隔", position = 2)
    var scope: String? = null,

    @ApiModelProperty("应用标识", position = 3)
    var identify: String? = null,

    @ApiModelProperty(value = "token 有效期，单位秒", required = true, position = 4)
    var accessTokenValiditySeconds: Int = 86400,

    @ApiModelProperty(value = "refresh token 有效期，单位秒", required = true, position = 5)
    var refreshTokenValiditySeconds: Int = 2592000
)
