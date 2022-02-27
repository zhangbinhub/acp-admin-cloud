package pers.acp.admin.oauth.po

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import pers.acp.admin.common.base.BaseQueryPo

/**
 * @author zhangbin by 2018-1-17 15:50
 * @since JDK 11
 */
@ApiModel("用户信息查询参数")
data class UserQueryPo(
    @ApiModelProperty(value = "用户名称", position = 1)
    var name: String? = null,
    @ApiModelProperty(value = "登录账号", position = 2)
    var loginNo: String? = null,
    @ApiModelProperty(value = "是否启用", position = 5)
    var enabled: Boolean? = null,
    @ApiModelProperty(value = "机构名称", position = 10)
    var orgName: String? = null,
    @ApiModelProperty(value = "角色名称", position = 11)
    var roleName: String? = null
) : BaseQueryPo()
