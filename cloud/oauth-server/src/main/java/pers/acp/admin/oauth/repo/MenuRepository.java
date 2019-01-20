package pers.acp.admin.oauth.repo;

import pers.acp.admin.oauth.base.OauthBaseRepository;
import pers.acp.admin.oauth.entity.Menu;

import java.util.List;

/**
 * @author zhangbin by 2018-1-17 17:46
 * @since JDK 11
 */
public interface MenuRepository extends OauthBaseRepository<Menu, String> {

    List<Menu> findByAppid(String appId);

    void deleteByIdInAndCovert(List<String> idList, boolean covert);

}
