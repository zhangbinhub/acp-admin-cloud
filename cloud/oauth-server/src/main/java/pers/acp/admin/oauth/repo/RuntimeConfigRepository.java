package pers.acp.admin.oauth.repo;

import pers.acp.admin.oauth.base.OauthBaseRepository;
import pers.acp.admin.oauth.entity.RuntimeConfig;

import java.util.List;
import java.util.Optional;

/**
 * @author zhangbin by 2018-1-16 23:46
 * @since JDK 11
 */
public interface RuntimeConfigRepository extends OauthBaseRepository<RuntimeConfig, String> {

    Optional<RuntimeConfig> findByName(String name);

    void deleteByIdInAndCovert(List<String> idList, boolean covert);

}
