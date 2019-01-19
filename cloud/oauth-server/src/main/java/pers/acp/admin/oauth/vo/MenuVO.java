package pers.acp.admin.oauth.vo;

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
@ApiModel("菜单配置详细信息")
public class MenuVO {

    @ApiModelProperty("菜单ID")
    private String id;

    @ApiModelProperty(value = "应用ID", required = true, position = 1)
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
