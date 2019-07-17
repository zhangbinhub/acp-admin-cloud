package pers.acp.admin.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhangbin by 2018-1-16 23:00
 * @since JDK 11
 */
@Entity
@Table(name = "t_runtimeconfig", indexes = {
        @Index(columnList = "name,enabled")
})
@ApiModel("运行配置")
public class RuntimeConfig {

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCovert() {
        return covert;
    }

    public void setCovert(boolean covert) {
        this.covert = covert;
    }

    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("配置ID")
    private String id;

    @Column(length = 100, unique = true, nullable = false)
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("值")
    private String value = "";

    @ApiModelProperty("描述")
    private String configDes = "";

    @Column(nullable = false)
    @ApiModelProperty("是否启用")
    private boolean enabled = true;

    @Column(nullable = false)
    @ApiModelProperty("是否可删除")
    private boolean covert = true;

}
