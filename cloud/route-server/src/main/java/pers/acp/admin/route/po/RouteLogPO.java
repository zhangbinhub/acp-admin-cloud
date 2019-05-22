package pers.acp.admin.route.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import pers.acp.admin.common.po.QueryParam;

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
@ApiModel("网关路由日志")
public class RouteLogPO {

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

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }

    @ApiModelProperty("客户端ip")
    private String remoteIp;

    @ApiModelProperty("网关ip")
    private String gatewayIp;

    @ApiModelProperty("请求路径")
    private String path;

    @ApiModelProperty("路由服务id")
    private String serverId;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;

    @ApiModelProperty("响应状态码")
    private Integer responseStatus;

    @ApiModelProperty(value = "分页查询参数", position = Integer.MAX_VALUE)
    private QueryParam queryParam;

}
