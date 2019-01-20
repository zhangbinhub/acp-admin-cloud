package pers.acp.admin.oauth.repo;

import pers.acp.admin.oauth.base.OauthBaseRepository;
import pers.acp.admin.oauth.entity.Role;

import java.util.List;

/**
 * @author zhangbin by 2018-1-17 17:48
 * @since JDK 11
 */
public interface RoleRepository extends OauthBaseRepository<Role, String> {

    List<Role> findAllByOrderBySortAsc();

    List<Role> findByAppidOrderBySortAsc(String appId);

    void deleteByIdIn(List<String> idList);

}
