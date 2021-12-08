package pers.acp.admin.workflow.base

import pers.acp.spring.boot.exceptions.ServerException
import kotlin.jvm.Throws

interface BaseWorkFlowNotify {
    @Throws(ServerException::class)
    fun doNotify(taskId: String, userId: String)
}