package pers.acp.admin.workflow.repo

import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.workflow.entity.MyProcessInstance
import java.util.*

interface MyProcessInstanceRepository : BaseRepository<MyProcessInstance, String> {
    fun findByUserIdAndProcessInstanceId(userId: String, processInstanceId: String): Optional<MyProcessInstance>
}