package pers.acp.admin.oauth.repo;

import pers.acp.admin.oauth.base.OauthBaseRepository;
import pers.acp.admin.oauth.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author zhangbin by 2018-1-17 17:48
 * @since JDK 11
 */
public interface UserRepository extends OauthBaseRepository<User, String> {

    Optional<User> findByLoginno(String loginno);

    Optional<User> findByMobile(String mobile);

    Optional<User> findByLoginnoAndIdNot(String loginno, String userId);

    Optional<User> findByMobileAndIdNot(String mobile, String userId);

    List<User> findByLevelsGreaterThan(int currLevels);

    void deleteByIdIn(List<String> idList);

}
