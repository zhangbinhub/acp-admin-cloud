package pers.acp.admin.workflow.constant;

import pers.acp.admin.common.constant.ModuleFuncCode;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.common.permission.BaseExpression;

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
public interface WorkFlowExpression extends BaseExpression {

    /**
     * 启动流程
     */
    String flowStart = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowStart + "')";

    /**
     * 获取流程待办任务
     */
    String flowPending = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowPending + "')";

    /**
     * 审批
     */
    String flowApprove = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowApprove + "')";

    /**
     * 获取流程处理历史
     */
    String flowHistory = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowHistory + "')";

    /**
     * 获取流程图
     */
    String flowDiagram = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowDiagram + "')";

}
