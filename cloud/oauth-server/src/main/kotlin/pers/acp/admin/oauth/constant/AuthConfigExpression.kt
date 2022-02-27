package pers.acp.admin.oauth.constant

import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.constant.RoleCode

/**
 * 定义权限配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object AuthConfigExpression {
    /**
     * 权限配置
     */
    const val authConfig =
        "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.authConfig + "')"

    /**
     * 权限新增
     */
    const val authAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.authAdd + "')"

    /**
     * 权限删除
     */
    const val authDelete =
        "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.authDelete + "')"

    /**
     * 权限更新
     */
    const val authUpdate =
        "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.authUpdate + "')"

    /**
     * 权限查询
     */
    const val authQuery =
        "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.authQuery + "')"
}