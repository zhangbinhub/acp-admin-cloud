package pers.acp.admin.common.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("流程实例")
data class ProcessInstanceVo(
        @ApiModelProperty(value = "流程实例id", position = 1)
        var processInstanceId: String? = null,

        @ApiModelProperty(value = "流程定义键", position = 2)
        var processDefinitionKey: String? = null,

        @ApiModelProperty(value = "业务键", position = 3)
        var businessKey: String? = null,

        @ApiModelProperty(value = "流程名称", position = 4)
        var flowName: String? = null,

        @ApiModelProperty(value = "标题", position = 5)
        var title: String? = null,

        @ApiModelProperty(value = "流程描述", position = 6)
        var description: String? = null,

        @ApiModelProperty(value = "发起人", position = 7)
        var startUser: UserVo? = null,

        @ApiModelProperty(value = "自定义参数", position = 8)
        var params: MutableMap<String, Any> = mutableMapOf(),

        @ApiModelProperty(value = "流程是否已结束", position = 9)
        var finished: Boolean = false,

        @ApiModelProperty(value = "开始时间", position = 10)
        var startTime: Long = 0,

        @ApiModelProperty(value = "结束时间", position = 11)
        var endTime: Long? = null
)
