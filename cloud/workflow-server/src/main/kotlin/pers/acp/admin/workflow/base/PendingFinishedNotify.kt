package pers.acp.admin.workflow.base

import pers.acp.spring.boot.exceptions.ServerException
import kotlin.jvm.Throws

abstract class PendingFinishedNotify : BaseWorkFlowNotify {
    @Throws(ServerException::class)
    override fun doProcessNotify(processInstanceId: String, userIdList: List<String>) {
    }
}