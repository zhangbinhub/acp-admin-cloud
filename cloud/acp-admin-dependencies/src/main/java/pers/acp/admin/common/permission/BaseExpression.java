package pers.acp.admin.common.permission;

import pers.acp.admin.common.code.RoleCode;

/**
 * 定义权限表达式
 *
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
public interface BaseExpression {

    /**
     * 仅超级管理员可执行
     */
    String adminOnly = "hasRole('" + RoleCode.ADMIN + "')";

}
