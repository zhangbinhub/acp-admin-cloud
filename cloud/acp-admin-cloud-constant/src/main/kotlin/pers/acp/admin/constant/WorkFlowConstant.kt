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
    //待发送用户部门级别，负数|零|正数；0-当前用户所在部门，-1上一级部门，-2上两级部门...依次类推，1下一级部门，2下两级部门...依次类推，多个code时使用“,”分隔
    const val orgLevel = "orgLevel"
    //待发送用户所属角色code，多个code时使用“,”分隔
    const val roleCode = "roleCode"
    //任务编码，用于自定义判断任务处理方式
    const val taskCode = "taskCode"
    //当前任务是否允许驳回
    const val reject = "isReject"
    //驳回至目标任务的定义ID，多个值时使用“,”分隔
    const val rejectToTask = "rejectToTask"
    /**
     * 工作流自定义参数
     */
    //节点任务候选人ID（一人或多人），多个候选人时使用“,”分隔；任务处理完成后需修改（变更为下一节点人或置空）
    const val candidateUser = "candidateUser"
    //节点任务处理人ID（只能一人）；任务处理完成后需修改（变更为下一节点人或置空）
    const val assigneeUser = "assigneeUser"
}