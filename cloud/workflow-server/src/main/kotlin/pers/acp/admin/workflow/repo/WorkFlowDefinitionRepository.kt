package pers.acp.admin.workflow.repo

import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.workflow.entity.WorkFlowDefinition

/**
 * @author zhangbin by 2018-1-17 17:44
 * @since JDK 11
 */
interface WorkFlowDefinitionRepository : BaseRepository<WorkFlowDefinition, String> {

    fun findAllByProcessKeyOrderByVersionDesc(processKey: String): MutableList<WorkFlowDefinition>

    fun deleteByIdIn(idList: MutableList<String>)

}
