package pers.acp.admin.common.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotBlank

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@ApiModel(value = "分页查询参数", description = "非查询请求时可为空")
data class QueryParam(
        @ApiModelProperty(value = "当前页号", required = true, position = 1)
        var currPage: Int = 1,

        @ApiModelProperty(value = "每页记录数", required = true, position = 2)
        var pageSize: Int = 10,

        @ApiModelProperty(value = "排序列名，多列以“,”分隔", required = true, position = 3)
        @get:NotBlank(message = "排序列名不能为空")
        var orderName: String? = null,

        @ApiModelProperty(value = "排序方式", allowableValues = "asc,desc", required = true, position = 4)
        @get:NotBlank(message = "排序方式不能为空")
        var orderCommond: String = "desc"
)
