package pers.acp.admin.common.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
@ApiModel("强制终止流程实例参数")
data class ProcessTerminationPo(
        @ApiModelProperty(value = "流程实例ID", required = true, position = 1)
        @get:NotBlank(message = "流程实例ID不能为空")
        var processInstanceId: String? = null,

        @ApiModelProperty(value = "原因", required = true, position = 2)
        @get:NotBlank(message = "原因不能为空")
        var reason: String? = null
)
