package pers.acp.admin.common.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("流程任务")
data class ProcessTaskVo(
        @ApiModelProperty(value = "流程实例id", position = 1)
        var processInstanceId: String? = null,

        @ApiModelProperty(value = "任务名称", position = 2)
        var name: String? = null,

        @ApiModelProperty(value = "任务id", position = 3)
        var taskId: String? = null,

        @ApiModelProperty(value = "任务定义键", position = 4)
        var taskDefinitionKey: String? = null,

        @ApiModelProperty(value = "父任务id", position = 5)
        var parentTaskId: String? = null,

        @ApiModelProperty(value = "执行实例id", position = 6)
        var executionId: String? = null,

        @ApiModelProperty(value = "业务键", position = 7)
        var businessKey: String? = null,

        @ApiModelProperty(value = "是否未被认领", position = 8)
        var unClaimed: Boolean = true,

        @ApiModelProperty(value = "处理人", position = 9)
        var user: UserVo? = null,

        @ApiModelProperty(value = "流程自定义参数", position = 10)
        var params: MutableMap<String, Any> = mutableMapOf(),

        @ApiModelProperty(value = "任务自定义参数", position = 11)
        var localParams: MutableMap<String, Any> = mutableMapOf(),

        @ApiModelProperty(value = "任务自定义属性，对应流程定义中的动态表单属性（key=名称,value=表达式的值）", position = 12)
        var properties: MutableMap<String, String> = mutableMapOf(),

        @ApiModelProperty(value = "任务创建时间", position = 13)
        var createTime: Long = 0,

        @ApiModelProperty(value = "任务领取时间", position = 14)
        var claimTime: Long? = null,

        @ApiModelProperty(value = "流程定义键", position = 15)
        var processDefinitionKey: String? = null,

        @ApiModelProperty(value = "流程名称", position = 16)
        @get:NotBlank(message = "流程名称不能为空")
        var flowName: String? = null,

        @ApiModelProperty(value = "标题", position = 17)
        @get:NotBlank(message = "标题不能为空")
        var title: String? = null,

        @ApiModelProperty(value = "流程描述", position = 18)
        var description: String? = null,

        @ApiModelProperty(value = "流程发起人", position = 19)
        var startUser: UserVo? = null,

        @ApiModelProperty(value = "任务拥有者", position = 20)
        var taskOwnerUser: UserVo? = null,

        @ApiModelProperty(value = "任务是否已委派", position = 21)
        var delegated: Boolean = false
)
