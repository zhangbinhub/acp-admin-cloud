package pers.acp.admin.workflow.notify

import org.springframework.stereotype.Component
import pers.acp.admin.workflow.base.PendingCreatedNotify
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import kotlin.jvm.Throws

@Component
class TestPendingCreatedNotify(private val logAdapter: LogAdapter) : PendingCreatedNotify() {
    @Throws(ServerException::class)
    override fun doTaskNotify(taskId: String, userIdList: List<String>) {
        logAdapter.info("待办生成通知：taskId【$taskId】，userId：$userIdList")
    }
}