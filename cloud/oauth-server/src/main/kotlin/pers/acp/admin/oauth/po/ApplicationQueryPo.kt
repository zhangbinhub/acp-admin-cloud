package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.base.BaseQueryPo

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
@ApiModel("应用配置查询参数")
data class ApplicationQueryPo(
        @ApiModelProperty(value = "应用名称", position = 1)
        var appName: String? = null
) : BaseQueryPo()
