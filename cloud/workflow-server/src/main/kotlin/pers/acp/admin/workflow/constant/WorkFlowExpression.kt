package pers.acp.admin.workflow.constant

import pers.acp.admin.common.constant.ModuleFuncCode
import pers.acp.admin.common.constant.RoleCode
import pers.acp.admin.common.permission.BaseExpression

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
object WorkFlowExpression : BaseExpression() {
    const val adminOnly = BaseExpression.adminOnly
    const val sysConfig = BaseExpression.sysConfig
    /**
     * 启动流程
     */
    const val flowStart = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowStart + "')"

    /**
     * 获取流程待办任务
     */
    const val flowPending = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowPending + "')"

    /**
     * 审批
     */
    const val flowApprove = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowApprove + "')"

    /**
     * 获取流程处理历史
     */
    const val flowHistory = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowHistory + "')"

    /**
     * 获取流程图
     */
    const val flowDiagram = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.ADMIN + "','" + ModuleFuncCode.flowDiagram + "')"
}