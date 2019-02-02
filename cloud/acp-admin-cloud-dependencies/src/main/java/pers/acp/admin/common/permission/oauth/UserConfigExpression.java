package pers.acp.admin.common.permission.oauth;

import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.common.permission.BaseExpression;

/**
 * 定义用户配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface UserConfigExpression extends BaseExpression {

    /**
     * 用户配置
     */
    String userConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userConfig + "')";

    /**
     * 用户新增
     */
    String userAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userAdd + "')";

    /**
     * 用户删除
     */
    String userDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userDelete + "')";

    /**
     * 用户更新
     */
    String userUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userUpdate + "')";

    /**
     * 用户查询
     */
    String userQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.userQuery + "')";

}
