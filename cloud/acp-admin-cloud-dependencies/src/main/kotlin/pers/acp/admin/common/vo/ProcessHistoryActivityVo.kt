package pers.acp.admin.common.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("流程历史记录")
data class ProcessHistoryActivityVo(
        @ApiModelProperty(value = "流程实例id", position = 1)
        var processInstanceId: String? = null,

        @ApiModelProperty(value = "活动id", position = 2)
        var activityId: String? = null,

        @ApiModelProperty(value = "活动名称", position = 3)
        var activityName: String? = null,

        @ApiModelProperty(value = "任务id", position = 4)
        var taskId: String? = null,

        @ApiModelProperty(value = "任务定义键", position = 5)
        var taskDefinitionKey: String? = null,

        @ApiModelProperty(value = "执行实例id", position = 6)
        var executionId: String? = null,

        @ApiModelProperty(value = "业务键", position = 7)
        var businessKey: String? = null,

        @ApiModelProperty(value = "处理人", position = 8)
        var user: UserVo? = null,

        @ApiModelProperty(value = "审批意见", position = 9)
        var comment: String? = null,

        @ApiModelProperty(value = "审批是否通过", position = 10)
        var pass: Boolean? = null,

        @ApiModelProperty(value = "流程自定义参数", position = 11)
        var params: MutableMap<String, Any> = mutableMapOf(),

        @ApiModelProperty(value = "任务自定义参数", position = 12)
        var localParams: MutableMap<String, Any> = mutableMapOf(),

        @ApiModelProperty(value = "开始时间", position = 13)
        var startTime: Long = 0,

        @ApiModelProperty(value = "结束时间", position = 14)
        var endTime: Long? = null
)
