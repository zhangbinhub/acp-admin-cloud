package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.Organization;
import pers.acp.admin.oauth.entity.Role;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.po.OrganizationPO;
import pers.acp.admin.oauth.repo.OrganizationRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.admin.oauth.vo.OrganizationVO;
import pers.acp.springboot.core.exceptions.ServerException;

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

    private Organization doSave(Organization organization, OrganizationPO organizationPO) {
        organization.setName(organizationPO.getName());
        organization.setCode(organizationPO.getCode());
        organization.setSort(organizationPO.getSort());
        organization.setParentid(organizationPO.getParentid());
        organization.setUserSet(new HashSet<>(userRepository.findAllById(organizationPO.getUserIds())));
        return organizationRepository.save(organization);
    }

    /**
     * 是否有编辑权限
     *
     * @param loginNo 登录帐号
     * @param orgIds  机构ID
     * @return true|false
     */
    private boolean isNotPermit(String loginNo, String... orgIds) {
        User user = findCurrUserInfo(loginNo);
        if (user != null) {
            return !user.getRoleSet().stream()
                    .map(Role::getCode)
                    .collect(Collectors.toList())
                    .contains(RoleCode.ADMIN)
                    && !user.getOrganizationMngSet().stream()
                    .map(Organization::getId)
                    .collect(Collectors.toList())
                    .containsAll(Arrays.asList(orgIds));
        }
        return true;
    }

    @Transactional
    public Organization doCreate(String loginNo, OrganizationPO organizationPO) throws ServerException {
        if (isNotPermit(loginNo, organizationPO.getParentid())) {
            throw new ServerException("没有权限做此操作，请联系系统管理员");
        }
        Organization organization = new Organization();
        return doSave(organization, organizationPO);
    }

    @Transactional
    public void doDelete(String loginNo, List<String> idList) throws ServerException {
        if (isNotPermit(loginNo, idList.toArray(new String[]{}))) {
            throw new ServerException("没有权限做此操作，请联系系统管理员；存在下级机构，不允许删除；");
        }
        List<Organization> organizationList = organizationRepository.findByParentidIn(idList);
        if (!organizationList.isEmpty()) {
            throw new ServerException("存在下级机构，不允许删除");
        }
        organizationRepository.deleteByIdIn(idList);
    }

    @Transactional
    public Organization doUpdate(String loginNo, OrganizationPO organizationPO) throws ServerException {
        Optional<Organization> organizationOptional = organizationRepository.findById(organizationPO.getId());
        if (organizationOptional.isEmpty()) {
            throw new ServerException("找不到机构信息");
        }
        Organization organization = organizationOptional.get();
        if (isNotPermit(loginNo, organization.getId())) {
            throw new ServerException("没有权限做此操作，请联系系统管理员");
        }
        return doSave(organization, organizationPO);
    }

    public List<Organization> getOrgList() {
        return formartOrgList(organizationRepository.findAllByOrderBySortAsc().stream().collect(Collectors.toMap(Organization::getId, organization -> organization)));
    }

    public OrganizationVO getOrgInfo(String orgId) throws ServerException {
        Optional<Organization> organizationOptional = organizationRepository.findById(orgId);
        if (organizationOptional.isEmpty()) {
            throw new ServerException("找不到机构信息");
        }
        Organization organization = organizationOptional.get();
        OrganizationVO organizationVO = new OrganizationVO();
        organizationVO.setId(organization.getId());
        organizationVO.setCode(organization.getCode());
        organizationVO.setName(organization.getName());
        organizationVO.setParentid(organization.getParentid());
        organizationVO.setSort(organization.getSort());
        organizationVO.setUserIds(organization.getUserSet().stream().map(User::getId).collect(Collectors.toList()));
        return organizationVO;
    }

    private List<Organization> formartOrgList(Map<String, Organization> organizationMap) {
        List<Organization> result = new ArrayList<>();
        organizationMap.forEach((id, organization) -> {
            if (organizationMap.containsKey(organization.getParentid())) {
                organizationMap.get(organization.getParentid()).getChildren().add(organization);
            } else {
                result.add(organization);
            }
        });
        return result;
    }

}
