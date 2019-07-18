package pers.acp.admin.route.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.po.QueryParam

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@ApiModel("网关路由日志")
data class RouteLogPo(
        @ApiModelProperty("客户端ip")
        var remoteIp: String? = null,

        @ApiModelProperty("网关ip")
        var gatewayIp: String? = null,

        @ApiModelProperty("请求路径")
        var path: String? = null,

        @ApiModelProperty("路由服务id")
        var serverId: String? = null,

        @ApiModelProperty("开始时间")
        var startTime: Long? = null,

        @ApiModelProperty("结束时间")
        var endTime: Long? = null,

        @ApiModelProperty("响应状态码")
        var responseStatus: Int? = null,

        @ApiModelProperty(value = "分页查询参数", position = Integer.MAX_VALUE)
        var queryParam: QueryParam? = null
)
