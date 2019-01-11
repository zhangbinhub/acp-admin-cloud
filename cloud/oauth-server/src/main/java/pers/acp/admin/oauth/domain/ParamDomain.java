package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.acp.admin.oauth.base.BaseDomain;
import pers.acp.admin.oauth.repo.RuntimeConfigRepository;
import pers.acp.admin.oauth.repo.UserRepository;

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@Service
public class ParamDomain extends BaseDomain {

    private final RuntimeConfigRepository runtimeConfigRepository;

    @Autowired
    public ParamDomain(UserRepository userRepository, RuntimeConfigRepository runtimeConfigRepository) {
        super(userRepository);
        this.runtimeConfigRepository = runtimeConfigRepository;
    }

}
