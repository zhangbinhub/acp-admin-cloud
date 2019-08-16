package pers.acp.admin.oauth.constant

import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.permission.BaseExpression

/**
 * 定义运行参数配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object RuntimeConfigExpression : BaseExpression() {
    const val superOnly = BaseExpression.superOnly
    const val sysConfig = BaseExpression.sysConfig
    /**
     * 运行参数配置
     */
    const val runtimeConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.runtimeConfig + "')"

    /**
     * 运行参数新增
     */
    const val runtimeAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.runtimeAdd + "')"

    /**
     * 运行参数删除
     */
    const val runtimeDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.runtimeDelete + "')"

    /**
     * 运行参数更新
     */
    const val runtimeUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.runtimeUpdate + "')"

    /**
     * 运行参数查询
     */
    const val runtimeQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.runtimeQuery + "')"
}