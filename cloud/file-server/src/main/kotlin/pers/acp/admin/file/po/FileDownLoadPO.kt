package pers.acp.admin.file.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
@ApiModel("文件下载参数")
data class FileDownLoadPO(
        @ApiModelProperty("文件路径")
        @get:NotBlank(message = "路径不能为空")
        var filePath: String? = null
)