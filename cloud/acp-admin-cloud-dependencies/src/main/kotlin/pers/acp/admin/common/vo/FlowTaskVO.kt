package pers.acp.admin.common.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import java.util.HashMap

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("流程任务")
data class FlowTaskVO(
        @ApiModelProperty(value = "流程实例id", position = 1)
        var processInstanceId: String? = null,

        @ApiModelProperty(value = "任务名称", position = 2)
        var name: String? = null,

        @ApiModelProperty(value = "任务id", position = 3)
        var taskId: String? = null,

        @ApiModelProperty(value = "父任务id", position = 4)
        var parentTaskId: String? = null,

        @ApiModelProperty(value = "执行实例id", position = 5)
        var executionId: String? = null,

        @ApiModelProperty(value = "业务键", position = 6)
        var businessKey: String? = null,

        @ApiModelProperty(value = "处理人id", position = 7)
        var userId: String? = null,

        @ApiModelProperty(value = "流程自定义参数", position = 8)
        var params: MutableMap<String, Any> = mutableMapOf(),

        @ApiModelProperty(value = "任务自定义参数", position = 9)
        var localParams: MutableMap<String, Any> = mutableMapOf(),

        @ApiModelProperty(value = "任务创建时间", position = 10)
        var createTime: Long = 0
)
