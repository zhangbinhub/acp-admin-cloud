package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.po.QueryParam

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@ApiModel("运行配置参数")
data class RuntimePo(

        @ApiModelProperty(value = "配置ID，更新时必填")
        var id: String? = null,

        @ApiModelProperty(value = "名称，查询时可为空", required = true, position = 1)
        @get:NotBlank(message = "参数名称不能为空")
        var name: String? = null,

        @ApiModelProperty(value = "值", position = 2)
        var value: String = "",

        @ApiModelProperty(value = "描述", position = 3)
        var configDes: String = "",

        @ApiModelProperty(value = "是否启用", position = 4)
        @get:NotNull(message = "是否启用不能为空")
        var enabled: Boolean? = null,

        @ApiModelProperty(value = "分页查询参数", position = Int.MAX_VALUE)
        var queryParam: QueryParam? = null

)
