package pers.acp.admin.config.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import pers.acp.admin.common.po.QueryParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhang by 01/03/2019
 * @since JDK 11
 */
@ApiModel("后台服务配置参数")
public class PropertiesPO {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfigApplication() {
        return configApplication;
    }

    public void setConfigApplication(String configApplication) {
        this.configApplication = configApplication;
    }

    public String getConfigProfile() {
        return configProfile;
    }

    public void setConfigProfile(String configProfile) {
        this.configProfile = configProfile;
    }

    public String getConfigLabel() {
        return configLabel;
    }

    public void setConfigLabel(String configLabel) {
        this.configLabel = configLabel;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
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

    @ApiModelProperty("配置ID")
    private String id;

    /**
     * 对应 spring.application.name
     */
    @ApiModelProperty(value = "服务名", position = 1)
    @NotBlank(message = "服务名不能为空")
    private String configApplication;

    /**
     * 对应 spring.profiles.active
     */
    @ApiModelProperty(value = "配置项", position = 2)
    @NotBlank(message = "配置项不能为空")
    private String configProfile;

    /**
     * 分支标签
     */
    @ApiModelProperty(value = "标签", position = 3)
    @NotBlank(message = "标签不能为空")
    private String configLabel;

    /**
     * 配置项键
     */
    @ApiModelProperty(value = "键", position = 4)
    @NotBlank(message = "键不能为空")
    private String configKey;

    /**
     * 配置项值
     */
    @ApiModelProperty(value = "值", position = 5)
    @NotBlank(message = "值不能为空")
    private String configValue;

    @ApiModelProperty(value = "描述", position = 6)
    private String configDes = "";

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用", position = 7)
    @NotNull(message = "是否启用不能为空")
    private Boolean enabled;


    @ApiModelProperty(value = "分页查询参数", position = Integer.MAX_VALUE)
    private QueryParam queryParam;

}
