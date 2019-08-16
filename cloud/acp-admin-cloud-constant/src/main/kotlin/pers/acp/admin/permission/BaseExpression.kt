package pers.acp.admin.permission

import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.constant.RoleCode

/**
 * 定义权限表达式
 *
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
open class BaseExpression {
    companion object {
        /**
         * 仅超级管理员可执行
         */
        const val superOnly = "hasRole('" + RoleCode.SUPER + "')"
        /**
         * 拥有系统配置权限
         */
        const val sysConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.sysConfig + "')"
    }
}
