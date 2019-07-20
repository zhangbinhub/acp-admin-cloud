package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhangbin by 2018-1-17 16:53
 * @since JDK 11
 */
@ApiModel("角色配置参数")
data class RolePo(

        @ApiModelProperty("角色ID，更新时必填")
        var id: String? = null,

        @ApiModelProperty(value = "应用ID，更新时可不填", required = true, position = 1)
        var appId: String? = null,

        @ApiModelProperty(value = "角色名称", required = true, position = 2)
        @get:NotBlank(message = "角色名称不能为空")
        var name: String? = null,

        @ApiModelProperty(value = "角色编码", required = true, position = 3)
        @get:NotBlank(message = "角色编码不能为空")
        var code: String? = null,

        @ApiModelProperty(value = "角色级别", required = true, position = 4)
        var levels: Int = 1,

        @ApiModelProperty(value = "序号", required = true, position = 5)
        var sort: Int = 0,

        @ApiModelProperty(value = "关联用户ID", position = 5)
        var userIds: MutableList<String> = mutableListOf(),

        @ApiModelProperty(value = "关联菜单ID", position = 6)
        var menuIds: MutableList<String> = mutableListOf(),

        @ApiModelProperty(value = "关联模块功能ID", position = 7)
        var moduleFuncIds: MutableList<String> = mutableListOf()

)
