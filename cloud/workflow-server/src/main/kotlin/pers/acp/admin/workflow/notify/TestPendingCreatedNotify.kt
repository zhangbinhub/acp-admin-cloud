package pers.acp.admin.workflow.notify

import io.github.zhangbinhub.acp.boot.exceptions.ServerException
import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import org.springframework.stereotype.Component
import pers.acp.admin.workflow.base.PendingCreatedNotify

@Component
class TestPendingCreatedNotify(private val logAdapter: LogAdapter) : PendingCreatedNotify() {
    @Throws(ServerException::class)
    override fun doTaskNotify(taskId: String, userIdList: List<String>) {
        logAdapter.info("待办生成通知：taskId【$taskId】，userId：$userIdList")
    }
}