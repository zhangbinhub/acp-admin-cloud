package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.repo.ModuleFuncRepository;
import pers.acp.admin.oauth.repo.UserRepository;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class ModuleFuncDomain extends OauthBaseDomain {

    private final ModuleFuncRepository moduleFuncRepository;

    @Autowired
    public ModuleFuncDomain(UserRepository userRepository, ModuleFuncRepository moduleFuncRepository) {
        super(userRepository);
        this.moduleFuncRepository = moduleFuncRepository;
    }

}
