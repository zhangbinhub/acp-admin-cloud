package pers.acp.admin.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_gateway_route")
@ApiModel("网关路由配置")
public class GateWayRoute {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRouteid() {
        return routeid;
    }

    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPredicates() {
        return predicates;
    }

    public void setPredicates(String predicates) {
        this.predicates = predicates;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("路由ID")
    @Column(nullable = false)
    private String routeid;

    @ApiModelProperty("路由URI")
    @Column(nullable = false)
    private String uri;

    @ApiModelProperty("路由断言")
    @Column(columnDefinition = "text", nullable = false)
    private String predicates;

    @ApiModelProperty("路由过滤器")
    @Column(columnDefinition = "text")
    private String filters;

    @ApiModelProperty("路由序号")
    private int orderNum = 0;

    @ApiModelProperty("是否启用")
    private boolean enabled;

    @ApiModelProperty("备注")
    private String remarks;

}
