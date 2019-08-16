package pers.acp.admin.workflow.constant

import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.constant.RoleCode
import pers.acp.admin.permission.BaseExpression

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
object WorkFlowExpression : BaseExpression() {
    const val superOnly = BaseExpression.superOnly
    const val sysConfig = BaseExpression.sysConfig
    /**
     * 启动流程
     */
    const val flowStart = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.flowStart + "')"

    /**
     * 获取流程待办任务
     */
    const val flowPending = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.flowPending + "')"

    /**
     * 审批
     */
    const val flowApprove = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.flowApprove + "')"

    /**
     * 获取流程处理历史
     */
    const val flowHistory = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.flowHistory + "')"

    /**
     * 获取流程图
     */
    const val flowDiagram = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.flowDiagram + "')"
}