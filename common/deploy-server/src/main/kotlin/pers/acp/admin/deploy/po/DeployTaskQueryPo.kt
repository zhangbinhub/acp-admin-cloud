package pers.acp.admin.deploy.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.base.BaseQueryPo

@ApiModel("部署任务查询参数")
data class DeployTaskQueryPo(
        @ApiModelProperty("任务名称")
        var name: String? = null,
        @ApiModelProperty("开始时间（执行）")
        var startTime: Long? = null,
        @ApiModelProperty("结束时间（执行）")
        var endTime: Long? = null
) : BaseQueryPo()