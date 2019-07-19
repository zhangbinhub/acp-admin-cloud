package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.po.QueryParam

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * @author zhangbin by 2018-1-17 15:50
 * @since JDK 11
 */
@ApiModel("用户信息参数")
data class UserPo(

        @ApiModelProperty("用户ID")
        var id: String? = null,

        @ApiModelProperty(value = "用户名称", required = true, position = 1)
        @NotBlank(message = "用户名称不能为空")
        var name: String? = null,

        @ApiModelProperty(value = "登录账号", required = true, position = 2)
        @NotBlank(message = "登录账号不能为空")
        var loginNo: String? = null,

        @ApiModelProperty(value = "手机号", required = true, position = 3)
        @NotBlank(message = "手机号不能为空")
        var mobile: String? = null,

        @ApiModelProperty(value = "用户级别", required = true, position = 4)
        @NotNull(message = "用户级别不能为空")
        var levels: Int? = null,

        @ApiModelProperty(value = "是否启用", required = true, position = 5)
        @NotNull(message = "是否启用不能为空")
        var enabled: Boolean? = null,

        @ApiModelProperty(value = "序号", required = true, position = 6)
        var sort: Int = 0,

        @ApiModelProperty(value = "所属机构ID", position = 7)
        var orgIds: MutableList<String> = mutableListOf(),

        @ApiModelProperty(value = "可管理机构ID", position = 8)
        var orgMngIds: MutableList<String> = mutableListOf(),

        @ApiModelProperty(value = "所属角色ID", position = 9)
        var roleIds: MutableList<String> = mutableListOf(),

        @ApiModelProperty(value = "机构名称，查询时使用", position = 10)
        var orgName: String? = null,

        @ApiModelProperty(value = "角色名称，查询时使用", position = 11)
        var roleName: String? = null,

        @ApiModelProperty(value = "分页查询参数", position = Int.MAX_VALUE)
        var queryParam: QueryParam? = null

)
