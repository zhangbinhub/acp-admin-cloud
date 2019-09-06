package pers.acp.admin.route.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.po.QueryParam
import javax.validation.constraints.NotBlank

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@ApiModel("网关路由日志")
data class RouteLogPo(
        @ApiModelProperty("客户端ip")
        @get:NotBlank(message = "客户端ip不能为空")
        var remoteIp: String? = null,

        @ApiModelProperty("网关ip")
        @get:NotBlank(message = "网关ip不能为空")
        var gatewayIp: String? = null,

        @ApiModelProperty("请求路径")
        @get:NotBlank(message = "请求路径不能为空")
        var path: String? = null,

        @ApiModelProperty("路由服务id")
        var serverId: String? = null,

        @ApiModelProperty("开始时间")
        var startTime: Long? = null,

        @ApiModelProperty("结束时间")
        var endTime: Long? = null,

        @ApiModelProperty("响应状态码")
        var responseStatus: Int? = null,

        @ApiModelProperty(value = "分页查询参数", position = Int.MAX_VALUE)
        var queryParam: QueryParam? = null
)
