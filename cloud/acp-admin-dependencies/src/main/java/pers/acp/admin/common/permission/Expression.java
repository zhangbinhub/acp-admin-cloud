package pers.acp.admin.common.permission;

/**
 * 定义权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface Expression {

    /**
     * 超级管理员可执行权限
     */
    String adminOnly = "hasRole('" + RoleCode.ADMIN + "')";

}
