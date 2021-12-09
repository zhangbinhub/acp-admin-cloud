package pers.acp.admin.workflow.base

import pers.acp.spring.boot.exceptions.ServerException
import kotlin.jvm.Throws

interface BaseWorkFlowNotify {
    @Throws(ServerException::class)
    fun doProcessNotify(processInstanceId: String, userIdList: List<String>)

    @Throws(ServerException::class)
    fun doTaskNotify(taskId: String, userIdList: List<String>)
}