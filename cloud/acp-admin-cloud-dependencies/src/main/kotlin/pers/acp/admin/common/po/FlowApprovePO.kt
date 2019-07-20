package pers.acp.admin.common.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("启动审批参数")
data class FlowApprovePO(
        @ApiModelProperty(value = "任务id", required = true, position = 1)
        @NotBlank(message = "任务id不能为空")
        var taskId: String? = null,

        @ApiModelProperty(value = "审批结果", required = true, example = "true", position = 2)
        @NotNull(message = "审批结果不能为空")
        var approved: Boolean? = null,

        @ApiModelProperty(value = "审批意见", required = true, position = 3)
        var comment: String? = null,

        @ApiModelProperty(value = "自定义流程参数", position = 4)
        var params: MutableMap<String, Any> = mutableMapOf(),

        @ApiModelProperty(value = "自定义任务参数", position = 5)
        var taskParams: MutableMap<String, Any> = mutableMapOf()
)
