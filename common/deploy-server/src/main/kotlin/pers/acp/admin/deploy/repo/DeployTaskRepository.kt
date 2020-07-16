package pers.acp.admin.deploy.repo

import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.deploy.entity.DeployTask

interface DeployTaskRepository : BaseRepository<DeployTask, String> {
    fun findAllByIdInAndExecTimeIsNotNull(idList: List<String>): List<DeployTask>
    fun deleteByIdIn(idList: List<String>)
}