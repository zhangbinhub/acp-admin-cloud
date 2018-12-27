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
        @Index(columnList = "confname,enabled")
})
@ApiModel("运行配置")
public class RuntimeConfig {

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
    @GenericGenerator(name = "idGenerator", strategy = "guid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 36, nullable = false)
    @ApiModelProperty("配置ID")
    private String id;

    @Column(length = 100, nullable = false)
    @ApiModelProperty("名称")
    private String confname;

    @ApiModelProperty("值")
    private String confvalue = "";

    @ApiModelProperty("描述")
    private String confdes = "";

    @Column(nullable = false)
    @ApiModelProperty("是否启用")
    private boolean enabled = true;

    @Column(nullable = false)
    @ApiModelProperty("是否可删除")
    private boolean covert = true;

}
