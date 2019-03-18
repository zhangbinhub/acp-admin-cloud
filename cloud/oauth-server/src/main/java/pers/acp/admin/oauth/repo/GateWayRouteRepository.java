package pers.acp.admin.oauth.repo;

import pers.acp.admin.oauth.base.OauthBaseRepository;
import pers.acp.admin.oauth.entity.GateWayRoute;

import java.util.List;

/**
 * @author zhangbin by 2018-1-16 23:46
 * @since JDK 11
 */
public interface GateWayRouteRepository extends OauthBaseRepository<GateWayRoute, String> {

    List<GateWayRoute> findAllByEnabled(boolean enabled);

    /**
     * 删除路由
     *
     * @param idList  id列表
     * @param enabled 启用状态
     */
    void deleteByIdInAndEnabled(List<String> idList, boolean enabled);

}
