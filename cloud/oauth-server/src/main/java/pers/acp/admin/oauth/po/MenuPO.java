package pers.acp.admin.oauth.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhang by 19/01/2019
 * @since JDK 11
 */
@ApiModel("菜单配置参数")
public class MenuPO {

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

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getOpentype() {
        return opentype;
    }

    public void setOpentype(int opentype) {
        this.opentype = opentype;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }

    @ApiModelProperty("菜单ID")
    private String id;

    @ApiModelProperty(value = "应用ID", required = true, position = 1)
    @NotBlank(message = "应用ID不能为空")
    private String appid;

    @ApiModelProperty(value = "菜单名称", required = true, position = 2)
    @NotBlank(message = "菜单名称不能为空")
    private String name;

    @ApiModelProperty(value = "菜单图标", required = true, position = 3)
    @NotBlank(message = "菜单图标不能为空")
    private String iconType;

    @ApiModelProperty(value = "链接路径", required = true, position = 4)
    @NotBlank(message = "链接路径不能为空")
    private String path;

    @ApiModelProperty(value = "上级菜单ID", required = true, position = 5)
    @NotBlank(message = "上级菜单ID不能为空")
    private String parentid;

    @ApiModelProperty(value = "菜单是否启用", required = true, position = 6)
    private boolean enabled = true;

    @ApiModelProperty(value = "链接打开模式；0-内嵌，1-新标签页", required = true, position = 7)
    @Min(value = 0, message = "打开模式只能为 0 或 1")
    @Max(value = 1, message = "打开模式只能为 0 或 1")
    private int opentype = 0;

    @ApiModelProperty(value = "序号", required = true, position = 8)
    private int sort;

    @ApiModelProperty(value = "关联角色ID", position = 9)
    private List<String> roleIds = new ArrayList<>();

}
