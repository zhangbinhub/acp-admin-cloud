package pers.acp.admin.oauth.domain;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.Organization;
import pers.acp.admin.oauth.entity.Role;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.po.UserPO;
import pers.acp.admin.oauth.repo.ApplicationRepository;
import pers.acp.admin.oauth.repo.OrganizationRepository;
import pers.acp.admin.oauth.repo.RoleRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.admin.oauth.token.SecurityTokenService;
import pers.acp.admin.oauth.vo.UserVO;
import pers.acp.core.CommonTools;
import pers.acp.core.security.SHA256Utils;
import pers.acp.spring.boot.exceptions.ServerException;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class UserDomain extends OauthBaseDomain {

    private static String DEFAULT_PASSWORD = "000000";

    private final ApplicationRepository applicationRepository;

    private final OrganizationRepository organizationRepository;

    private final RoleRepository roleRepository;

    private final SecurityTokenService securityTokenService;

    @Autowired
    public UserDomain(UserRepository userRepository, ApplicationRepository applicationRepository, OrganizationRepository organizationRepository, RoleRepository roleRepository, SecurityTokenService securityTokenService) {
        super(userRepository);
        this.applicationRepository = applicationRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.securityTokenService = securityTokenService;
    }

    public boolean isAdmin(OAuth2Authentication user) {
        User currUserInfo = findCurrUserInfo(user.getName());
        return isAdmin(currUserInfo);
    }

    private void validatePermit(String loginNo, UserPO userPO, Set<Role> roleSetPO, boolean isCreate) throws ServerException {
        User currUserInfo = findCurrUserInfo(loginNo);
        if (!isAdmin(currUserInfo)) {
            if (currUserInfo.getLevels() >= userPO.getLevels()) {
                throw new ServerException("不能编辑级别比自身大的用户信息");
            }
            for (Organization organization : currUserInfo.getOrganizationMngSet()) {
                if (!userPO.getOrgIds().contains(organization.getId())) {
                    throw new ServerException("没有权限编辑机构【" + organization.getName() + "】下的用户，请联系系统管理员");
                }
                if (!userPO.getOrgMngIds().contains(organization.getId())) {
                    throw new ServerException("没有权限编辑机构【" + organization.getName() + "】下的用户，请联系系统管理员");
                }
            }
            Map<String, Integer> roleMinLevel = getRoleMinLevel(currUserInfo);
            for (Role role : roleSetPO) {
                if (!roleMinLevel.containsKey(role.getAppid()) || roleMinLevel.get(role.getAppid()) >= role.getLevels()) {
                    throw new ServerException("没有权限编辑角色【" + role.getName() + "】，请联系系统管理员");
                }
            }
        } else {
            if (isCreate) {
                if (currUserInfo.getLevels() >= userPO.getLevels()) {
                    throw new ServerException("不能创建级别比自身大的用户");
                }
            }
        }
    }

    private User doSave(User user, UserPO userPO) {
        user.setMobile(userPO.getMobile());
        user.setName(userPO.getName());
        user.setEnabled(userPO.isEnabled());
        user.setLevels(userPO.getLevels());
        user.setSort(userPO.getSort());
        user.setOrganizationSet(new HashSet<>(organizationRepository.findAllById(userPO.getOrgIds())));
        user.setOrganizationMngSet(new HashSet<>(organizationRepository.findAllById(userPO.getOrgMngIds())));
        return doSaveUser(user);
    }

    @Transactional
    public User doSaveUser(User user) {
        return userRepository.save(user);
    }

    public User getMobileForOtherUser(String mobile, String userId) {
        return userRepository.findByMobileAndIdNot(mobile, userId).orElse(null);
    }

    public List<User> findModifiableUserList(String loginNo) {
        User user = findCurrUserInfo(loginNo);
        if (isAdmin(user)) {
            return userRepository.findAll();
        } else {
            return userRepository.findByLevelsGreaterThan(user.getLevels());
        }
    }

    @Transactional
    public User doCreate(String loginNo, UserPO userPO) throws ServerException {
        Set<Role> roleSet = new HashSet<>(roleRepository.findAllById(userPO.getRoleIds()));
        validatePermit(loginNo, userPO, roleSet, true);
        User checkUser = userRepository.findByLoginno(userPO.getLoginno()).orElse(null);
        if (checkUser != null) {
            throw new ServerException("登录账号已存在，请重新输入");
        }
        checkUser = userRepository.findByMobile(userPO.getMobile()).orElse(null);
        if (checkUser != null) {
            throw new ServerException("手机号码已存在，请重新输入");
        }
        User user = new User();
        user.setLoginno(userPO.getLoginno());
        user.setPassword(SHA256Utils.encrypt(SHA256Utils.encrypt(DEFAULT_PASSWORD) + userPO.getLoginno()));
        user.setRoleSet(roleSet);
        return doSave(user, userPO);
    }

    @Transactional
    public User doUpdate(String loginNo, UserPO userPO) throws ServerException {
        Set<Role> roleSet = new HashSet<>(roleRepository.findAllById(userPO.getRoleIds()));
        validatePermit(loginNo, userPO, roleSet, false);
        Optional<User> userOptional = userRepository.findById(userPO.getId());
        if (userOptional.isEmpty()) {
            throw new ServerException("找不到用户信息");
        }
        User user = userOptional.get();
        User checkUser = userRepository.findByLoginnoAndIdNot(userPO.getLoginno(), user.getId()).orElse(null);
        if (checkUser != null) {
            throw new ServerException("登录账号已存在，请重新输入");
        }
        checkUser = userRepository.findByMobileAndIdNot(userPO.getMobile(), user.getId()).orElse(null);
        if (checkUser != null) {
            throw new ServerException("手机号码已存在，请重新输入");
        }
        if (!user.getLoginno().equals(userPO.getLoginno())) {
            user.setLoginno(userPO.getLoginno());
            user.setPassword(SHA256Utils.encrypt(SHA256Utils.encrypt(DEFAULT_PASSWORD) + userPO.getLoginno()));
            removeToken(userPO.getLoginno());
        }
        user.setRoleSet(roleSet);
        return doSave(user, userPO);
    }

    @Transactional
    public void doUpdatePwd(String loginNo, String userId) throws ServerException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ServerException("找不到用户信息");
        }
        User user = userOptional.get();
        User currUserInfo = findCurrUserInfo(loginNo);
        if (!isAdmin(currUserInfo)) {
            if (currUserInfo.getLevels() >= user.getLevels()) {
                throw new ServerException("不能修改级别比自身大或相等的用户信息");
            }
        }
        user.setPassword(SHA256Utils.encrypt(SHA256Utils.encrypt(DEFAULT_PASSWORD) + user.getLoginno()));
        userRepository.save(user);
        removeToken(loginNo);
    }

    @Transactional
    public void doDelete(String loginNo, List<String> idList) throws ServerException {
        User user = findCurrUserInfo(loginNo);
        if (idList.contains(user.getId())) {
            throw new ServerException("不能删除自己");
        }
        List<User> userList = userRepository.findAllById(idList);
        if (!isAdmin(user)) {
            for (User item : userList) {
                if (user.getLevels() >= item.getLevels()) {
                    throw new ServerException("没有权限做此操作，请联系系统管理员");
                }
            }
        }
        userRepository.deleteByIdIn(idList);
        userList.forEach(userInfo -> removeToken(userInfo.getLoginno()));
    }

    private void removeToken(String loginNo) {
        applicationRepository.findAllByOrderByAppnameAsc().forEach(application -> securityTokenService.removeTokensByAppIdAndLoginNo(application.getId(), loginNo));
    }

    public Page<UserVO> doQuery(UserPO userPO) {
        return userRepository.findAll((Specification<User>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!CommonTools.isNullStr(userPO.getLoginno())) {
                predicateList.add(criteriaBuilder.equal(root.get("loginno").as(String.class), userPO.getLoginno()));
            }
            if (!CommonTools.isNullStr(userPO.getName())) {
                predicateList.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + userPO.getName() + "%"));
            }
            if (userPO.isEnabled() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("enabled"), userPO.isEnabled()));
            }
            if (!CommonTools.isNullStr(userPO.getOrgName())) {
                Join<User, Organization> joinOrg = root.join("organizationSet", JoinType.LEFT);
                predicateList.add(criteriaBuilder.like(joinOrg.get("name").as(String.class), "%" + userPO.getOrgName() + "%"));
            }
            if (!CommonTools.isNullStr(userPO.getRoleName())) {
                Join<User, Role> joinOrg = root.join("roleSet", JoinType.LEFT);
                predicateList.add(criteriaBuilder.like(joinOrg.get("name").as(String.class), "%" + userPO.getRoleName() + "%"));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
        }, buildPageRequest(userPO.getQueryParam()))
                .map(user -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(user, userVO);
                    return userVO;
                });
    }

    public User getUserInfo(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

}
