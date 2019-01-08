package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.acp.admin.oauth.base.BaseDomain;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
public class UserDomain extends BaseDomain {

    @Autowired
    public UserDomain(UserRepository userRepository) {
        super(userRepository);
    }

    public User doSaveUser(User user) {
        return userRepository.save(user);
    }

}
