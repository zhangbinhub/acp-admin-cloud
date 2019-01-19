package pers.acp.admin.oauth.repo;

import pers.acp.admin.oauth.base.OauthBaseRepository;
import pers.acp.admin.oauth.entity.ModuleFunc;

import java.util.List;

/**
 * @author zhangbin by 2018-1-17 17:46
 * @since JDK 11
 */
public interface ModuleFuncRepository extends OauthBaseRepository<ModuleFunc, String> {

    List<ModuleFunc> findByAppid(String appId);

}
