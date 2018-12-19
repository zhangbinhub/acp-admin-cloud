package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findCurrUserInfo(String loginNo) {
        return userRepository.findByLoginno(loginNo).orElse(null);
    }

}
