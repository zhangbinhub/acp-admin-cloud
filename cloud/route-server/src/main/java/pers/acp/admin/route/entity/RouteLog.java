package pers.acp.admin.route.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_gateway_route_log")
@ApiModel("网关路由日志")
public class RouteLog {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getGatewayIp() {
        return gatewayIp;
    }

    public void setGatewayIp(String gatewayIp) {
        this.gatewayIp = gatewayIp;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
    }

    public Long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Long processTime) {
        this.processTime = processTime;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("ID")
    private String id;

    @Column(nullable = false)
    @ApiModelProperty("客户端ip")
    private String remoteIp;

    @Column(nullable = false)
    @ApiModelProperty("网关ip")
    private String gatewayIp;

    @Column(nullable = false)
    @ApiModelProperty("请求路径")
    private String path;

    @ApiModelProperty("路由服务id")
    private String serverId;

    @ApiModelProperty("目标服务url")
    private String targetUri;

    @Column(nullable = false)
    @ApiModelProperty("请求时间")
    private Long requestTime;

    @ApiModelProperty("处理时长")
    private Long processTime;

    @ApiModelProperty("响应时间")
    private Long responseTime;

    @ApiModelProperty("响应状态码")
    private Integer responseStatus;

}
