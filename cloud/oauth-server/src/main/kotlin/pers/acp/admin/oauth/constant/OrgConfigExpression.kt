package pers.acp.admin.oauth.constant

import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.permission.BaseExpression

/**
 * 定义机构配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
object OrgConfigExpression : BaseExpression() {
    const val superOnly = BaseExpression.superOnly
    const val sysConfig = BaseExpression.sysConfig
    /**
     * 机构配置
     */
    const val orgConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.orgConfig + "')"

    /**
     * 机构新增
     */
    const val orgAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.orgAdd + "')"

    /**
     * 机构删除
     */
    const val orgDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.orgDelete + "')"

    /**
     * 机构更新
     */
    const val orgUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.orgUpdate + "')"

    /**
     * 机构查询
     */
    const val orgQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.orgQuery + "')"
}