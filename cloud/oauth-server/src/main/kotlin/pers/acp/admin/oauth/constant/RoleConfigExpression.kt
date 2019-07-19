package pers.acp.admin.oauth.constant

import pers.acp.admin.common.constant.ModuleFuncCode
import pers.acp.admin.common.constant.RoleCode
import pers.acp.admin.common.permission.BaseExpression

/**
 * 定义角色配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object RoleConfigExpression : BaseExpression() {
    const val adminOnly = BaseExpression.adminOnly
    const val sysConfig = BaseExpression.sysConfig
    /**
     * 角色配置
     */
    const val roleConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleConfig + "')"

    /**
     * 角色新增
     */
    const val roleAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleAdd + "')"

    /**
     * 角色删除
     */
    const val roleDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleDelete + "')"

    /**
     * 角色更新
     */
    const val roleUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleUpdate + "')"

    /**
     * 角色查询
     */
    const val roleQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleQuery + "')"
}