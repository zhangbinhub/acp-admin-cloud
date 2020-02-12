package pers.acp.admin.route.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
@ApiModel("网关路由配置参数")
data class RoutePo(
        @ApiModelProperty("ID")
        var id: String? = null,

        @ApiModelProperty(value = "路由ID", required = true, position = 1)
        @get:NotBlank(message = "路由ID不能为空")
        var routeId: String? = null,

        @ApiModelProperty(value = "路由URI", required = true, position = 2)
        @get:NotBlank(message = "路由URI不能为空")
        var uri: String? = null,

        @ApiModelProperty(value = "路由断言", required = true, position = 3)
        @get:NotBlank(message = "路由断言不能为空")
        var predicates: String? = null,

        @ApiModelProperty(value = "路由过滤器", position = 4)
        var filters: String? = null,

        @ApiModelProperty("元数据", position = 5)
        var metadata: String? = null,

        @ApiModelProperty(value = "路由序号", position = 6)
        var orderNum: Int = 0,

        @ApiModelProperty(value = "是否启用", required = true, position = 7)
        var enabled: Boolean? = null,

        @ApiModelProperty(value = "备注", position = 8)
        var remarks: String? = null
)
