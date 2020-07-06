package pers.acp.admin.workflow.constant

import pers.acp.admin.constant.ModuleFuncCode
import pers.acp.admin.constant.RoleCode

/**
 * @author zhang by 14/06/2019
 * @since JDK 11
 */
object WorkFlowExpression {
    /**
     * 流程定义
     */
    const val flowDefinition = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.flowDefinition + "')"

    /**
     * 获取流程待办任务
     */
    const val flowPending = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.flowPending + "')"

    /**
     * 流程处理
     */
    const val flowProcess = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.flowProcess + "')"

    /**
     * 流程终止
     */
    const val flowTermination = "hasAnyAuthority('" + RoleCode.prefix + RoleCode.SUPER + "','" + ModuleFuncCode.flowAdmin + "','" + ModuleFuncCode.flowProcess + "')"
}