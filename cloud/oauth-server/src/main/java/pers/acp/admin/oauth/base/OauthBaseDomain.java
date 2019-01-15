package pers.acp.admin.oauth.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.base.BaseDomain;
import pers.acp.admin.common.po.QueryParam;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.core.CommonTools;

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@Transactional(readOnly = true)
public class OauthBaseDomain extends BaseDomain {

    protected final UserRepository userRepository;

    @Autowired
    public OauthBaseDomain(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findCurrUserInfo(String loginNo) {
        return userRepository.findByLoginno(loginNo).orElse(null);
    }

    public User findUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

}
