package pers.acp.admin.common.base

import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.po.QueryParam
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * @author zhang by 26/11/2019
 * @since JDK 11
 */
abstract class BaseQueryPo {
    @ApiModelProperty(value = "分页查询参数", required = true, position = Int.MAX_VALUE)
    @get:NotNull(message = "分页查询参数不能为空")
    @field:Valid
    var queryParam: QueryParam? = null
}