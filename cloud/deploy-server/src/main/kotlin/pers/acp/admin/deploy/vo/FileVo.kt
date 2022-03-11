package pers.acp.admin.deploy.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("文件信息")
data class FileVo(
    @ApiModelProperty(value = "是否是文件夹")
    var directory: Boolean = false,
    @ApiModelProperty(value = "文件名")
    var name: String = "",
    @ApiModelProperty(value = "文件大小，单位：字节")
    var size: Long = 0,
    @ApiModelProperty(value = "最后修改时间")
    var lastModified: Long = 0
)
