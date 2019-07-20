package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.po.QueryParam

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

        @ApiModelProperty(value = "token 有效期，单位秒", required = true, position = 2)
        var accessTokenValiditySeconds: Int = 86400,

        @ApiModelProperty(value = "refresh token 有效期，单位秒", required = true, position = 3)
        var refreshTokenValiditySeconds: Int = 2592000,

        @ApiModelProperty(value = "分页查询参数", position = Int.MAX_VALUE)
        var queryParam: QueryParam? = null
)
