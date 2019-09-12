package pers.acp.admin.log.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator

import javax.persistence.*

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_log_gateway_route", uniqueConstraints = [UniqueConstraint(columnNames = ["logId", "requestTime"])])
@ApiModel("网关路由日志")
class RouteLog {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("ID")
    var id: String = ""

    @Column(nullable = false)
    @ApiModelProperty("路由消息随机ID")
    var logId: String? = null

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

    @ApiModelProperty("目标服务ip")
    var targetIp: String? = null

    @ApiModelProperty("目标服务url")
    var targetUri: String? = null

    @ApiModelProperty("目标服务请求路径")
    var targetPath: String? = null

    @ApiModelProperty("请求方法")
    var method: String? = null

    @ApiModelProperty("请求token")
    var token: String? = null

    @ApiModelProperty("客户端id")
    var clientId: String? = null

    @ApiModelProperty("客户端名称")
    var clientName: String? = null

    @ApiModelProperty("客户端标识")
    var identify: String? = null

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
