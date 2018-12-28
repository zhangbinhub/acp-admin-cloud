package pers.acp.admin.common.permission;

/**
 * 定义权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface Expression {

    String adminOnly = "hasRole(" + RoleCode.ADMIN + ")";

}
