package pers.acp.admin.oauth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 19/01/2019
 * @since JDK 11
 */
@ApiModel("模块功能配置详细信息")
public class ModuleFuncVO {

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

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty(value = "应用ID", position = 1)
    private String appid;

    @ApiModelProperty(value = "模块名称", position = 2)
    @NotBlank(message = "模块名称不能为空")
    private String name;

    @ApiModelProperty(value = "模块编码", position = 3)
    @NotBlank(message = "模块编码不能为空")
    private String code;

    @ApiModelProperty(value = "上级ID", position = 4)
    @NotBlank(message = "上级ID不能为空")
    private String parentid;

    @ApiModelProperty(value = "关联角色ID", position = 9)
    private List<String> roleIds = new ArrayList<>();

}
