package pers.acp.admin.common.permission.oauth;

import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.common.permission.BaseExpression;

/**
 * 定义权限配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface AuthConfigExpression extends BaseExpression {

    /**
     * 权限配置
     */
    String authConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authConfig + "')";

    /**
     * 权限新增
     */
    String authAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authAdd + "')";

    /**
     * 权限删除
     */
    String authDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authDelete + "')";

    /**
     * 权限更新
     */
    String authUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authUpdate + "')";

    /**
     * 权限查询
     */
    String authQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.authQuery + "')";

}
