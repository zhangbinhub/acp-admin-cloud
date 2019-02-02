package pers.acp.admin.common.permission.oauth;

import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.common.permission.BaseExpression;

/**
 * 定义运行参数配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface RuntimeConfigExpression extends BaseExpression {

    /**
     * 运行参数配置
     */
    String runtimeConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeConfig + "')";

    /**
     * 运行参数新增
     */
    String runtimeAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeAdd + "')";

    /**
     * 运行参数删除
     */
    String runtimeDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeDelete + "')";

    /**
     * 运行参数更新
     */
    String runtimeUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeUpdate + "')";

    /**
     * 运行参数查询
     */
    String runtimeQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.runtimeQuery + "')";

}
