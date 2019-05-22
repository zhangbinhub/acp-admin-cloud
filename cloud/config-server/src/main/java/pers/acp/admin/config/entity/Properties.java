package pers.acp.admin.config.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhang by 27/02/2019
 * @since JDK 11
 */
@Entity
@Table(name = "t_properties", indexes = {
        @Index(columnList = "configApplication,configProfile,configLabel,enabled")
})
@ApiModel("后台服务配置")
public class Properties {

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("配置ID")
    private String id;

    /**
     * 对应 spring.application.name
     */
    @Column(nullable = false)
    @ApiModelProperty("服务名")
    private String configApplication;

    /**
     * 对应 spring.profiles.active
     */
    @Column(nullable = false)
    @ApiModelProperty("配置项")
    private String configProfile;

    /**
     * 分支标签
     */
    @Column(nullable = false)
    @ApiModelProperty("标签")
    private String configLabel;

    /**
     * 配置项键
     */
    @Column(nullable = false)
    @ApiModelProperty("键")
    private String configKey;

    /**
     * 配置项值
     */
    @Column(nullable = false)
    @ApiModelProperty("值")
    private String configValue;

    @ApiModelProperty("描述")
    private String configDes = "";

    /**
     * 是否启用
     */
    @Column(nullable = false)
    @ApiModelProperty("是否启用")
    private boolean enabled;

}
