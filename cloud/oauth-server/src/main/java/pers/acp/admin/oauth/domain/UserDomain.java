package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class UserDomain extends OauthBaseDomain {

    @Autowired
    public UserDomain(UserRepository userRepository) {
        super(userRepository);
    }

    @Transactional
    public User doSaveUser(User user) {
        return userRepository.save(user);
    }

}
