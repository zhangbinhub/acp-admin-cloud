package pers.acp.admin.common.permission;

import pers.acp.admin.common.code.FuncCode;
import pers.acp.admin.common.code.ModuleCode;
import pers.acp.admin.common.code.RoleCode;

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
public interface AppConfigExpression extends BaseExpression {

    /**
     * 应用配置
     */
    String appConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleCode.appConfig + "')";

    /**
     * 应用新增
     */
    String appAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + FuncCode.appAdd + "')";

    /**
     * 应用删除
     */
    String appDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + FuncCode.appDelete + "')";

    /**
     * 应用更新
     */
    String appUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + FuncCode.appUpdate + "')";

    /**
     * 应用查询
     */
    String appQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + FuncCode.appQuery + "')";

    /**
     * 应用更新密钥
     */
    String appUpdateSecret = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + FuncCode.appUpdateSecret + "')";

}
