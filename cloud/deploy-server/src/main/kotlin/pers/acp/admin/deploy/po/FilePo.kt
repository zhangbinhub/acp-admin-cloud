package pers.acp.admin.deploy.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

@ApiModel("文件操作参数")
data class FilePo(
    @ApiModelProperty("路径", required = false)
    var path: String? = null,
    @ApiModelProperty("名称", required = true)
    @get:NotBlank(message = "名称不能为空")
    var name: String? = null
)