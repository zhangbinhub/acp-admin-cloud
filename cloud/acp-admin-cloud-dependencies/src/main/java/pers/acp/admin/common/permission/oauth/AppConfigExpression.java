package pers.acp.admin.common.permission.oauth;

import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.common.permission.BaseExpression;

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
public interface AppConfigExpression extends BaseExpression {

    /**
     * 应用配置
     */
    String appConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appConfig + "')";

    /**
     * 应用新增
     */
    String appAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appAdd + "')";

    /**
     * 应用删除
     */
    String appDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appDelete + "')";

    /**
     * 应用更新
     */
    String appUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appUpdate + "')";

    /**
     * 应用查询
     */
    String appQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appQuery + "')";

    /**
     * 应用更新密钥
     */
    String appUpdateSecret = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.appUpdateSecret + "')";

}
