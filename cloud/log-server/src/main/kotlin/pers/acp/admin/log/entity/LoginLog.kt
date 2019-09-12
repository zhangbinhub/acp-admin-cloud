package pers.acp.admin.log.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import pers.acp.core.CommonTools
import pers.acp.core.task.timer.Calculation

import javax.persistence.*

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_log_login",
        uniqueConstraints = [UniqueConstraint(columnNames = ["logId", "requestTime"])],
        indexes = [
            Index(columnList = "userId"),
            Index(columnList = "clientId,loginDate"),
            Index(columnList = "userId,clientId,loginDate")
        ])
@ApiModel("登录日志")
class LoginLog {

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("ID")
    var id: String = ""

    @Column(nullable = false)
    @ApiModelProperty("路由消息随机ID")
    var logId: String = ""

    @Column(nullable = false)
    @ApiModelProperty("客户端ip")
    var remoteIp: String = ""

    @ApiModelProperty("网关ip")
    var gatewayIp: String? = null

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

    @Column(nullable = false)
    @ApiModelProperty("请求方法")
    var method: String = ""

    @ApiModelProperty("请求token")
    var token: String? = null

    @Column(nullable = false)
    @ApiModelProperty("客户端id")
    var clientId: String = ""

    @Column(nullable = false)
    @ApiModelProperty("客户端名称")
    var clientName: String = ""

    @ApiModelProperty("客户端标识")
    var identify: String? = null

    @Column(nullable = false)
    @ApiModelProperty("请求时间")
    var requestTime: Long = System.currentTimeMillis()

    @ApiModelProperty("处理时长")
    var processTime: Long? = null

    @ApiModelProperty("响应时间")
    var responseTime: Long? = null

    @ApiModelProperty("响应状态码")
    var responseStatus: Int? = null

    @Column(nullable = false)
    @ApiModelProperty("用户id")
    var userId: String = ""

    @Column(nullable = false)
    @ApiModelProperty("用户登录号")
    var loginNo: String = ""

    @ApiModelProperty("用户名称")
    var userName: String? = null

    @Column(nullable = false)
    @ApiModelProperty("登录日期")
    var loginDate: String = CommonTools.getDateTimeString(null, Calculation.DATE_FORMAT)

}
