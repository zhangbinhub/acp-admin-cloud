package pers.acp.admin.common.permission;

import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.common.constant.RoleCode;

/**
 * 定义运行参数配置权限表达式
 *
 * @author zhang by 28/12/2018
 * @since JDK 11
 */
public interface ParamConfigExpression extends BaseExpression {

    /**
     * 运行参数配置
     */
    String paramConfig = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.paramConfig + "')";

    /**
     * 运行参数新增
     */
    String paramAdd = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.paramAdd + "')";

    /**
     * 运行参数删除
     */
    String paramDelete = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.paramDelete + "')";

    /**
     * 运行参数更新
     */
    String paramUpdate = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.paramUpdate + "')";

    /**
     * 运行参数查询
     */
    String paramQuery = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.paramQuery + "')";

}
