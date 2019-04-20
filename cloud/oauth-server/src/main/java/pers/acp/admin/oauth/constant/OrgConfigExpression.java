package pers.acp.admin.oauth.constant;

import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.common.permission.BaseExpression;

/**
 * 定义机构配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface OrgConfigExpression extends BaseExpression {

    /**
     * 机构配置
     */
    String orgConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.orgConfig + "')";

    /**
     * 机构新增
     */
    String orgAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.orgAdd + "')";

    /**
     * 机构删除
     */
    String orgDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.orgDelete + "')";

    /**
     * 机构更新
     */
    String orgUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.orgUpdate + "')";

    /**
     * 机构查询
     */
    String orgQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.orgQuery + "')";

}
