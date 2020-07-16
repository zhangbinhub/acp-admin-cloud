package pers.acp.admin.deploy.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

@ApiModel("部署任务参数")
data class DeployTaskPo(
        @ApiModelProperty("ID")
        var id: String? = null,
        @ApiModelProperty("任务名称", required = true)
        @get:NotBlank(message = "任务名称不能为空")
        var name: String? = null,
        @ApiModelProperty("脚本文件", required = true)
        @get:NotBlank(message = "脚本文件不能为空")
        var scriptFile: String? = null,
        @ApiModelProperty("执行的服务器IP正则表达式")
        var serverIpRegex: String? = null,
        @ApiModelProperty("备注")
        var remarks: String? = null
)