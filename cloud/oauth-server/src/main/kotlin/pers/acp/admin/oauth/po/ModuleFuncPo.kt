package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 19/01/2019
 * @since JDK 11
 */
@ApiModel("模块功能配置参数")
data class ModuleFuncPo(

        @ApiModelProperty("ID")
        var id: String? = null,

        @ApiModelProperty(value = "应用ID", required = true, position = 1)
        @get:NotBlank(message = "应用ID不能为空")
        var appId: String? = null,

        @ApiModelProperty(value = "模块名称", required = true, position = 2)
        @get:NotBlank(message = "模块名称不能为空")
        var name: String? = null,

        @ApiModelProperty(value = "模块编码", required = true, position = 3)
        @get:NotBlank(message = "模块编码不能为空")
        var code: String? = null,

        @ApiModelProperty(value = "上级ID", required = true, position = 4)
        @get:NotBlank(message = "上级ID不能为空")
        var parentId: String? = null,

        @ApiModelProperty(value = "关联角色ID", position = 9)
        var roleIds: MutableList<String> = mutableListOf()

)
