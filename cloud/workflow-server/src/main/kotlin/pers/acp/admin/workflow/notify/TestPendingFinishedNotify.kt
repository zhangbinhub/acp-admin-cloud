package pers.acp.admin.workflow.notify

import org.springframework.stereotype.Component
import pers.acp.admin.workflow.base.PendingFinishedNotify
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import kotlin.jvm.Throws

@Component
class TestPendingFinishedNotify(private val logAdapter: LogAdapter) : PendingFinishedNotify {
    @Throws(ServerException::class)
    override fun doNotify(taskId: String, userId: String) {
        logAdapter.debug("待办完成通知：taskId【$taskId】，userId【$userId】")
    }
}