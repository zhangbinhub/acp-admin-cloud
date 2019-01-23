package pers.acp.admin.oauth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import pers.acp.admin.common.po.QueryParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@ApiModel("运行配置参数")
public class RuntimePO {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getConfigDes() {
        return configDes;
    }

    public void setConfigDes(String configDes) {
        this.configDes = configDes;
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

    @ApiModelProperty(value = "配置ID，更新时必填")
    private String id;

    @ApiModelProperty(value = "名称，查询时可为空", required = true, position = 1)
    @NotBlank(message = "参数名称不能为空")
    private String name;

    @ApiModelProperty(value = "值", position = 2)
    private String value = "";

    @ApiModelProperty(value = "描述", position = 3)
    private String configDes = "";

    @ApiModelProperty(value = "是否启用", position = 4)
    @NotNull(message = "是否启用不能为空")
    private Boolean enabled;

    @ApiModelProperty(value = "分页查询参数", position = Integer.MAX_VALUE)
    private QueryParam queryParam;

}
