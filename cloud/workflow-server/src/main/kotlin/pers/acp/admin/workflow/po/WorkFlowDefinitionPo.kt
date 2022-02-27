package pers.acp.admin.workflow.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 20/12/2019
 * @since JDK 11
 */
@ApiModel("工作流定义信息参数")
data class WorkFlowDefinitionPo(
    @ApiModelProperty("ID")
    var id: String? = null,
    @ApiModelProperty("备注")
    var remarks: String? = null
)