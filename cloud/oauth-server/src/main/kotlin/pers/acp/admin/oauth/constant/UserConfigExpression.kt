package pers.acp.admin.oauth.constant

import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.permission.BaseExpression

/**
 * 定义用户配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object UserConfigExpression {
    /**
     * 用户配置
     */
    const val userConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.userConfig + "')"

    /**
     * 用户新增
     */
    const val userAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.userAdd + "')"

    /**
     * 用户删除
     */
    const val userDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.userDelete + "')"

    /**
     * 用户更新
     */
    const val userUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.userUpdate + "')"

    /**
     * 用户查询
     */
    const val userQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.userQuery + "')"
}