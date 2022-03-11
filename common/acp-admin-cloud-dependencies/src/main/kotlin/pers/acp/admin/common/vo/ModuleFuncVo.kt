package pers.acp.admin.common.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 19/01/2019
 * @since JDK 11
 */
@ApiModel("模块功能配置详细信息")
data class ModuleFuncVo(

    @ApiModelProperty("ID")
    var id: String? = null,

    @ApiModelProperty(value = "应用ID", position = 1)
    var appId: String? = null,

    @ApiModelProperty(value = "模块名称", position = 2)
    @NotBlank(message = "模块名称不能为空")
    var name: String? = null,

    @ApiModelProperty(value = "模块编码", position = 3)
    @NotBlank(message = "模块编码不能为空")
    var code: String? = null

)
