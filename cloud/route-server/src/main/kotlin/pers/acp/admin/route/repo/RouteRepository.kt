package pers.acp.admin.route.repo

import org.springframework.data.repository.NoRepositoryBean
import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.route.entity.Route

/**
 * @author zhangbin by 2018-1-16 23:46
 * @since JDK 11
 */
interface RouteRepository : BaseRepository<Route, String> {

    fun findAllByEnabled(enabled: Boolean): List<Route>

    /**
     * 删除路由
     *
     * @param idList  id列表
     * @param enabled 启用状态
     */
    fun deleteByIdInAndEnabled(idList: List<String>, enabled: Boolean)

}
