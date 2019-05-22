package pers.acp.admin.route.repo;

import pers.acp.admin.common.base.BaseRepository;
import pers.acp.admin.route.entity.Route;

import java.util.List;

/**
 * @author zhangbin by 2018-1-16 23:46
 * @since JDK 11
 */
public interface RouteRepository extends BaseRepository<Route, String> {

    List<Route> findAllByEnabled(boolean enabled);

    /**
     * 删除路由
     *
     * @param idList  id列表
     * @param enabled 启用状态
     */
    void deleteByIdInAndEnabled(List<String> idList, boolean enabled);

}
