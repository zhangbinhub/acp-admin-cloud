package pers.acp.admin.oauth.base;

import org.springframework.beans.factory.annotation.Autowired;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
public class BaseDomain {

    protected final UserRepository userRepository;

    @Autowired
    public BaseDomain(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findCurrUserInfo(String loginNo) {
        return userRepository.findByLoginno(loginNo).orElse(null);
    }

    public User findUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

}
