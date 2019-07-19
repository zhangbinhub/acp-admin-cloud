package pers.acp.admin.oauth.constant

import pers.acp.admin.common.constant.ModuleFuncCode
import pers.acp.admin.common.constant.RoleCode
import pers.acp.admin.common.permission.BaseExpression

/**
 * 定义权限配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object AuthConfigExpression : BaseExpression() {
    const val adminOnly = BaseExpression.adminOnly
    const val sysConfig = BaseExpression.sysConfig
    /**
     * 权限配置
     */
    const val authConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authConfig + "')"

    /**
     * 权限新增
     */
    const val authAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authAdd + "')"

    /**
     * 权限删除
     */
    const val authDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authDelete + "')"

    /**
     * 权限更新
     */
    const val authUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authUpdate + "')"

    /**
     * 权限查询
     */
    const val authQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authQuery + "')"
}