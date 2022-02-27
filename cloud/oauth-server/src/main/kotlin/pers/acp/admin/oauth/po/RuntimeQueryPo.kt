package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.base.BaseQueryPo

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@ApiModel("运行配置查询参数")
data class RuntimeQueryPo(
    @ApiModelProperty(value = "名称，查询时可为空", position = 1)
    var name: String? = null,
    @ApiModelProperty(value = "值", position = 2)
    var value: String? = null,
    @ApiModelProperty(value = "是否启用，查询时可为空", position = 4)
    var enabled: Boolean? = null
) : BaseQueryPo()
