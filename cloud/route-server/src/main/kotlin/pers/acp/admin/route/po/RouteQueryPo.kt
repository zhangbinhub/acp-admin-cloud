package pers.acp.admin.route.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.base.BaseQueryPo

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
@ApiModel("网关路由配置查询参数")
data class RouteQueryPo(
        @ApiModelProperty(value = "路由ID", position = 1)
        @get:NotBlank(message = "路由ID不能为空")
        var routeId: String? = null,
        @ApiModelProperty(value = "是否启用", position = 6)
        var enabled: Boolean? = null
) : BaseQueryPo()
