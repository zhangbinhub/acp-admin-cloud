package pers.acp.admin.common.permission.oauth;

import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.common.permission.BaseExpression;

/**
 * 定义角色配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface RoleConfigExpression extends BaseExpression {

    /**
     * 角色配置
     */
    String roleConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleConfig + "')";

    /**
     * 角色新增
     */
    String roleAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleAdd + "')";

    /**
     * 角色删除
     */
    String roleDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleDelete + "')";

    /**
     * 角色更新
     */
    String roleUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleUpdate + "')";

    /**
     * 角色查询
     */
    String roleQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.roleQuery + "')";

}
