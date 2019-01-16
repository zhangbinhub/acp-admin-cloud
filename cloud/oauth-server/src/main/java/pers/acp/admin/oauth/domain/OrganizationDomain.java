package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.repo.OrganizationRepository;
import pers.acp.admin.oauth.repo.UserRepository;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class OrganizationDomain extends OauthBaseDomain {

    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationDomain(UserRepository userRepository, OrganizationRepository organizationRepository) {
        super(userRepository);
        this.organizationRepository = organizationRepository;
    }
}
