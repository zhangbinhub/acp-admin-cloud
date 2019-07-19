package pers.acp.admin.route.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_gateway_route_log")
@ApiModel("网关路由日志")
class RouteLog {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("ID")
    var id: String = ""

    @Column(nullable = false)
    @ApiModelProperty("客户端ip")
    var remoteIp: String? = null

    @Column(nullable = false)
    @ApiModelProperty("网关ip")
    var gatewayIp: String? = null

    @Column(nullable = false)
    @ApiModelProperty("请求路径")
    var path: String? = null

    @ApiModelProperty("路由服务id")
    var serverId: String? = null

    @ApiModelProperty("目标服务url")
    var targetUri: String? = null

    @Column(nullable = false)
    @ApiModelProperty("请求时间")
    var requestTime: Long? = null

    @ApiModelProperty("处理时长")
    var processTime: Long? = null

    @ApiModelProperty("响应时间")
    var responseTime: Long? = null

    @ApiModelProperty("响应状态码")
    var responseStatus: Int? = null

}
