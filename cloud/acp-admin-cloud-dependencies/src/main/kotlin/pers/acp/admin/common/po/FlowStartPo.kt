package pers.acp.admin.common.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("启动流程参数")
data class FlowStartPo(
        @ApiModelProperty(value = "流程定义键", required = true, position = 1)
        @get:NotBlank(message = "流程定义键不能为空")
        var processDefinitionKey: String? = null,

        @ApiModelProperty(value = "业务键", required = true, position = 2)
        @get:NotBlank(message = "业务键不能为空")
        var businessKey: String? = null,

        @ApiModelProperty(value = "自定义参数", position = 3)
        var params: MutableMap<String, Any> = mutableMapOf()
)
