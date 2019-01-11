package pers.acp.admin.oauth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import pers.acp.admin.common.po.QueryParam;

import javax.validation.constraints.NotBlank;

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@ApiModel("运行配置参数")
public class ParamPO {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfname() {
        return confname;
    }

    public void setConfname(String confname) {
        this.confname = confname;
    }

    public String getConfvalue() {
        return confvalue;
    }

    public void setConfvalue(String confvalue) {
        this.confvalue = confvalue;
    }

    public String getConfdes() {
        return confdes;
    }

    public void setConfdes(String confdes) {
        this.confdes = confdes;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public QueryParam getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }

    @ApiModelProperty(value = "配置ID")
    private String id;

    @ApiModelProperty(value = "名称，查询时可为空", required = true, position = 1)
    @NotBlank(message = "参数名称不能为空")
    private String confname;

    @ApiModelProperty(value = "值", position = 2)
    private String confvalue = "";

    @ApiModelProperty(value = "描述", position = 3)
    private String confdes = "";

    @ApiModelProperty(value = "是否启用", position = 4)
    private Boolean enabled;

    @ApiModelProperty(value = "分页查询参数", position = Integer.MAX_VALUE)
    private QueryParam queryParam;

}
