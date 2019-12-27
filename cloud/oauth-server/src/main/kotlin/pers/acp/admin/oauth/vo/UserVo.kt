package pers.acp.admin.oauth.vo

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * @author zhangbin by 2018-1-17 15:50
 * @since JDK 11
 */
@ApiModel("用户信息详情")
data class UserVo(

        @ApiModelProperty("用户ID")
        var id: String? = null,

        @ApiModelProperty(value = "用户名称", position = 1)
        var name: String? = null,

        @ApiModelProperty(value = "登录号", position = 2)
        var loginNo: String? = null,

        @ApiModelProperty(value = "手机号", position = 3)
        var mobile: String? = null,

        @ApiModelProperty(value = "用户级别", position = 4)
        var levels: Int = 0,

        @ApiModelProperty(value = "是否启用", position = 5)
        var enabled: Boolean = false,

        @ApiModelProperty(value = "序号", position = 6)
        var sort: Int = 0,

        @ApiModelProperty(value = "所属机构", position = 7)
        var organizationSet: MutableSet<OrganizationVo> = mutableSetOf(),

        @ApiModelProperty(value = "可管理的机构", position = 8)
        var organizationMngSet: MutableSet<OrganizationVo> = mutableSetOf(),

        @ApiModelProperty(value = "所属角色", position = 9)
        var roleSet: MutableSet<RoleVo> = mutableSetOf()

)
