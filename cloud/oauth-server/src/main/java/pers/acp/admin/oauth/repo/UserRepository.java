package pers.acp.admin.oauth.repo;

import pers.acp.admin.oauth.base.BaseRepository;
import pers.acp.admin.oauth.entity.User;

import java.util.Optional;

/**
 * @author zhangbin by 2018-1-17 17:48
 * @since JDK 11
 */
public interface UserRepository extends BaseRepository<User, String> {

    Optional<User> findByLoginno(String loginno);

}
