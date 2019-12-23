package pers.acp.admin.common.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 04/11/2019
 * @since JDK 11
 */
@ApiModel("自定义分页查询结果")
data class CustomerQueryPageVo<T>(
        @ApiModelProperty(value = "内容")
        var content: List<T> = listOf(),
        @ApiModelProperty(value = "总数")
        var totalElements: Long = 0,
        @ApiModelProperty(value = "当前页号")
        var currPage: Long = 0,
        @ApiModelProperty(value = "每页记录数")
        var pageSize: Long = 0
)