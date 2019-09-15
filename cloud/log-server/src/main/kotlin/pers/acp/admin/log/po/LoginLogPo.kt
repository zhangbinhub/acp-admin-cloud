package pers.acp.admin.log.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.po.QueryParam

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@ApiModel("登录日志查询参数")
data class LoginLogPo(
        @ApiModelProperty("是否查询往日历史数据，true-往日历史，false-当日数据")
        var history: Boolean = false,

        @ApiModelProperty("客户端ip")
        var remoteIp: String? = null,

        @ApiModelProperty("网关ip")
        var gatewayIp: String? = null,

        @ApiModelProperty("请求路径")
        var path: String? = null,

        @ApiModelProperty("路由服务id")
        var serverId: String? = null,

        @ApiModelProperty("客户端名称")
        var clientName: String? = null,

        @ApiModelProperty("操作用户名称")
        var userName: String? = null,

        @ApiModelProperty("开始时间")
        var startTime: Long? = null,

        @ApiModelProperty("结束时间")
        var endTime: Long? = null,

        @ApiModelProperty(value = "分页查询参数", position = Int.MAX_VALUE)
        var queryParam: QueryParam? = null
)
