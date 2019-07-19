package pers.acp.admin.oauth.constant

import pers.acp.admin.common.constant.ModuleFuncCode
import pers.acp.admin.common.constant.RoleCode
import pers.acp.admin.common.permission.BaseExpression

/**
 * 定义运行参数配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object RuntimeConfigExpression : BaseExpression() {
    const val adminOnly = BaseExpression.adminOnly
    const val sysConfig = BaseExpression.sysConfig
    /**
     * 运行参数配置
     */
    const val runtimeConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeConfig + "')"

    /**
     * 运行参数新增
     */
    const val runtimeAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeAdd + "')"

    /**
     * 运行参数删除
     */
    const val runtimeDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeDelete + "')"

    /**
     * 运行参数更新
     */
    const val runtimeUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeUpdate + "')"

    /**
     * 运行参数查询
     */
    const val runtimeQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeQuery + "')"
}