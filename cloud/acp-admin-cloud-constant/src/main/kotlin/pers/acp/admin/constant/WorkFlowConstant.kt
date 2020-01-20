package pers.acp.admin.constant

object WorkFlowConstant {
    /**
     * 工作流任务自定义字段
     */
    //当前任务是否可以转办（任务处理人变更，处理完毕后进入下一节点）
    const val transfer = "isTransfer"
    //当前任务是否可以委派他人办理（任务处理人变更，处理完毕后返回至当前处理人继续办理）
    const val delegate = "isDelegate"
    //是否手动选择分配处理人（或候选人）
    const val selectUser = "selectUser"
    //待发送用户部门级别；0-当前用户所在部门，1-上一级部门，2-上两级部门...依次类推
    const val orgLevel = "orgLevel"
    //待发送用户所属角色code，多个code时使用“,”分隔
    const val roleCode = "roleCode"
    /**
     * 工作流自定义参数
     */
    //节点任务候选人ID（一人或多人），多个候选人时使用“,”分隔
    const val candidateUser = "candidateUser"
    //节点任务处理人ID（只能一人）
    const val assigneeUser = "assigneeUser"
}