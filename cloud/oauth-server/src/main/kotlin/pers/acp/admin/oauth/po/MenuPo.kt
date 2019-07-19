package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

/**
 * @author zhang by 19/01/2019
 * @since JDK 11
 */
@ApiModel("菜单配置参数")
data class MenuPo(
        @ApiModelProperty("菜单ID")
        var id: String? = null,

        @ApiModelProperty(value = "应用ID", required = true, position = 1)
        @NotBlank(message = "应用ID不能为空")
        var appId: String? = null,

        @ApiModelProperty(value = "菜单名称", required = true, position = 2)
        @NotBlank(message = "菜单名称不能为空")
        var name: String? = null,

        @ApiModelProperty(value = "菜单图标", required = true, position = 3)
        @NotBlank(message = "菜单图标不能为空")
        var iconType: String? = null,

        @ApiModelProperty(value = "链接路径", required = true, position = 4)
        @NotBlank(message = "链接路径不能为空")
        var path: String? = null,

        @ApiModelProperty(value = "上级菜单ID", required = true, position = 5)
        @NotBlank(message = "上级菜单ID不能为空")
        var parentId: String? = null,

        @ApiModelProperty(value = "菜单是否启用", required = true, position = 6)
        var enabled: Boolean = true,

        @ApiModelProperty(value = "链接打开模式；0-内嵌，1-新标签页", required = true, position = 7)
        @Min(value = 0, message = "打开模式只能为 0 或 1")
        @Max(value = 1, message = "打开模式只能为 0 或 1")
        var openType: Int = 0,

        @ApiModelProperty(value = "序号", required = true, position = 8)
        var sort: Int = 0,

        @ApiModelProperty(value = "关联角色ID", position = 9)
        var roleIds: MutableList<String> = mutableListOf()
)