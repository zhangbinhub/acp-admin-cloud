package pers.acp.admin.workflow.base

import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import kotlin.jvm.Throws

abstract class PendingCreatedNotify : BaseWorkFlowNotify {
    @Throws(ServerException::class)
    override fun doProcessNotify(processInstanceId: String, userIdList: List<String>) {
    }
}