package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.repo.RoleRepository;
import pers.acp.admin.oauth.repo.UserRepository;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class RoleDomain extends OauthBaseDomain {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleDomain(UserRepository userRepository, RoleRepository roleRepository) {
        super(userRepository);
        this.roleRepository = roleRepository;
    }
}
