package pers.acp.admin.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @author zhangbin by 2018-1-17 17:10
 * @since JDK 11
 */
@Entity
@Table(name = "t_module_func", indexes = {@Index(columnList = "code,appid")})
@ApiModel("模块功能信息")
public class ModuleFunc {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
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
    @ApiModelProperty("ID")
    private String id;

    @Column(length = 36, nullable = false)
    @ApiModelProperty("应用ID")
    private String appid;

    @Column(nullable = false)
    @ApiModelProperty("模块名称")
    private String name;

    @Column(length = 100, nullable = false)
    @ApiModelProperty("模块编码")
    private String code;

    @Column(length = 36, nullable = false)
    @ApiModelProperty("上级ID")
    private String parentid = "";

    @Column(nullable = false)
    @ApiModelProperty("是否可删除")
    private boolean covert = true;

}
