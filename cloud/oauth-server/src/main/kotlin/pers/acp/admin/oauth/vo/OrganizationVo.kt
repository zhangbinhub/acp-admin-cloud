package pers.acp.admin.oauth.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhang by 17/01/2019
 * @since JDK 11
 */
@ApiModel("机构详细信息")
data class OrganizationVo(

        @ApiModelProperty("机构ID")
        var id: String? = null,

        @ApiModelProperty(value = "机构名称", position = 1)
        var name: String? = null,

        @ApiModelProperty(value = "机构编码", position = 2)
        var code: String? = null,

        @ApiModelProperty(value = "上级机构ID", position = 3)
        var parentId: String? = null,

        @ApiModelProperty(value = "序号", position = 4)
        var sort: Int = 0,

        @ApiModelProperty(value = "关联用户ID", position = 5)
        var userIds: MutableList<String> = mutableListOf()

)
