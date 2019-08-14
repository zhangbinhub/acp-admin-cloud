package pers.acp.admin.oauth.constant

import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.permission.BaseExpression

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
object AppConfigExpression : BaseExpression() {
    const val adminOnly = BaseExpression.adminOnly
    const val sysConfig = BaseExpression.sysConfig
    /**
     * 应用配置
     */
    const val appConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appConfig + "')"

    /**
     * 应用新增
     */
    const val appAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appAdd + "')"

    /**
     * 应用删除
     */
    const val appDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appDelete + "')"

    /**
     * 应用更新
     */
    const val appUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appUpdate + "')"

    /**
     * 应用查询
     */
    const val appQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appQuery + "')"

    /**
     * 应用更新密钥
     */
    const val appUpdateSecret = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appUpdateSecret + "')"
}