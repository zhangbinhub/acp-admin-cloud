package pers.acp.admin.oauth.constant

import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.permission.BaseExpression

/**
 * 定义角色配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object RoleConfigExpression {
    /**
     * 角色配置
     */
    const val roleConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.roleConfig + "')"

    /**
     * 角色新增
     */
    const val roleAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.roleAdd + "')"

    /**
     * 角色删除
     */
    const val roleDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.roleDelete + "')"

    /**
     * 角色更新
     */
    const val roleUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.roleUpdate + "')"

    /**
     * 角色查询
     */
    const val roleQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.roleQuery + "')"
}