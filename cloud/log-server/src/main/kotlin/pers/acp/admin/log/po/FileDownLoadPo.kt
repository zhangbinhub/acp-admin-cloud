package pers.acp.admin.log.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.validation.constraints.NotBlank

@ApiModel("文件下载参数")
data class FileDownLoadPo(
        @ApiModelProperty("文件名称", required = true)
        @get:NotBlank(message = "文件名称不能为空")
        var fileName: String? = null
)