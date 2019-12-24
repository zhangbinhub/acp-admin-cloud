package pers.acp.admin.common.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("启动流程参数")
data class ProcessStartPo(
        @ApiModelProperty(value = "流程定义键", required = true, position = 1)
        @get:NotBlank(message = "流程定义键不能为空")
        var processDefinitionKey: String? = null,

        @ApiModelProperty(value = "业务键", required = true, position = 2)
        @get:NotBlank(message = "业务键不能为空")
        var businessKey: String? = null,

        @ApiModelProperty(value = "标题", required = true, position = 3)
        @get:NotBlank(message = "标题不能为空")
        var title: String? = null,

        @ApiModelProperty(value = "流程描述", required = true, position = 4)
        @get:NotBlank(message = "流程描述不能为空")
        var description: String? = null,

        @ApiModelProperty(value = "发起人", required = true, position = 5)
        @get:NotBlank(message = "发起人不能为空")
        var startUserId: String? = null,

        @ApiModelProperty(value = "自定义参数", position = 6)
        var params: MutableMap<String, Any> = mutableMapOf()
)
