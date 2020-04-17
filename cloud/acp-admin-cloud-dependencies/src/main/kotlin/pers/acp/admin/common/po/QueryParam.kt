package pers.acp.admin.common.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

import javax.validation.constraints.NotNull

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@ApiModel(value = "分页查询参数")
data class QueryParam(
        @ApiModelProperty(value = "当前页号", required = true, position = 1)
        @get:NotNull(message = "当前页号不能为空")
        var currPage: Int? = 1,

        @ApiModelProperty(value = "每页记录数", required = true, position = 2)
        @get:NotNull(message = "每页记录数不能为空")
        var pageSize: Int? = 10,

        @ApiModelProperty(value = "排序列名，多列以“,”分隔", position = 3)
        var orderName: String? = null,

        @ApiModelProperty(value = "排序方式", allowableValues = "asc,desc", position = 4)
        var orderCommand: String? = "desc"
)
