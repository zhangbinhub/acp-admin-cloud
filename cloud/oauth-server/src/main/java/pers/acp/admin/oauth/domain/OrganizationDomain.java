package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.Organization;
import pers.acp.admin.oauth.repo.OrganizationRepository;
import pers.acp.admin.oauth.repo.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<Organization> getOrgList() {
        List<Organization> result = new ArrayList<>();
        List<Organization> organizationList = organizationRepository.findAll();
        if (organizationList.size() > 0) {
            Map<String, Organization> organizationMap = organizationList.stream().collect(Collectors.toMap(Organization::getId, menu -> menu));
            organizationMap.forEach((id, organization) -> {
                if (organizationMap.containsKey(organization.getParentid())) {
                    organizationMap.get(organization.getParentid()).getChildren().add(organization);
                } else {
                    result.add(organization);
                }
            });
        }
        sortOrganizationList(result);
        return result;
    }

    private void sortOrganizationList(List<Organization> organizationList) {
        organizationList.forEach(organization -> {
            if (!organization.getChildren().isEmpty()) {
                sortOrganizationList(organization.getChildren());
            }
        });
        organizationList.sort(Comparator.comparingInt(Organization::getSort));
    }

}
