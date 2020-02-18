package pers.acp.admin.common.po

import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.base.BaseQueryPo

/**
 * @author zhang by 20/12/2019
 * @since JDK 11
 */
data class ProcessQueryPo(
        @ApiModelProperty(value = "流程定义键")
        var processDefinitionKeys: MutableList<String>? = null,
        @ApiModelProperty(value = "流程实例ID")
        var processInstanceIds: MutableList<String>? = null,
        @ApiModelProperty(value = "发起人")
        var startUserId: String? = null,
        @ApiModelProperty(value = "匹配创建时间（开始）")
        var startTime: Long? = null,
        @ApiModelProperty(value = "匹配创建时间（结束）")
        var endTime: Long? = null
) : BaseQueryPo()