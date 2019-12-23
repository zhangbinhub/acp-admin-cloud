package pers.acp.admin.workflow.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.base.BaseQueryPo

/**
 * @author zhang by 20/12/2019
 * @since JDK 11
 */
@ApiModel("工作流定义信息查询")
data class WorkFlowDefinitionQueryPo(
        @ApiModelProperty("资源文件名")
        var resourceName: String? = null,
        @ApiModelProperty("流程定义键")
        var processKey: String? = null,
        @ApiModelProperty("流程名称")
        var name: String? = null
) : BaseQueryPo()