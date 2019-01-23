package pers.acp.admin.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
@ApiModel("运行配置信息")
public class RuntimeConfigVO {

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("值")
    private String value = "";

    @ApiModelProperty("描述")
    private String configDes = "";

    @ApiModelProperty("是否启用")
    private boolean enabled = true;

}
