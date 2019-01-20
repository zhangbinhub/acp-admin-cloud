package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.Menu;
import pers.acp.admin.oauth.entity.ModuleFunc;
import pers.acp.admin.oauth.entity.Role;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.po.RolePO;
import pers.acp.admin.oauth.repo.MenuRepository;
import pers.acp.admin.oauth.repo.ModuleFuncRepository;
import pers.acp.admin.oauth.repo.RoleRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.admin.oauth.vo.RoleVO;
import pers.acp.springboot.core.exceptions.ServerException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class RoleDomain extends OauthBaseDomain {

    private final RoleRepository roleRepository;

    private final MenuRepository menuRepository;

    private final ModuleFuncRepository moduleFuncRepository;

    @Autowired
    public RoleDomain(UserRepository userRepository, RoleRepository roleRepository, MenuRepository menuRepository, ModuleFuncRepository moduleFuncRepository) {
        super(userRepository);
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
        this.moduleFuncRepository = moduleFuncRepository;
    }

    /**
     * 获取指定用户所属角色中最高级别
     *
     * @param loginNo 登录账号
     * @return 级别
     */
    private int getCurrUserMinLevel(String loginNo) {
        User user = findCurrUserInfo(loginNo);
        if (user != null) {
            final int[] level = {Integer.MAX_VALUE};
            user.getRoleSet().forEach(role -> {
                if (level[0] > role.getLevels()) {
                    level[0] = role.getLevels();
                }
            });
            return level[0];
        }
        return Integer.MAX_VALUE;
    }

    public List<Role> getRoleList() {
        return roleRepository.findAllByOrderBySortAsc();
    }

    public List<Role> getRoleListByAppId(String appId) {
        return roleRepository.findByAppidOrderBySortAsc(appId);
    }

    private Role doSave(Role role, RolePO rolePO) {
        role.setName(rolePO.getName());
        role.setCode(rolePO.getCode());
        role.setSort(rolePO.getSort());
        role.setLevels(rolePO.getLevels());
        role.setUserSet(new HashSet<>(userRepository.findAllById(rolePO.getUserIds())));
        role.setMenuSet(new HashSet<>(menuRepository.findAllById(rolePO.getMenuIds())));
        role.setModuleFuncSet(new HashSet<>(moduleFuncRepository.findAllById(rolePO.getModuleFuncIds())));
        return roleRepository.save(role);
    }

    @Transactional
    public Role doCreate(RolePO rolePO, String loginNo) throws ServerException {
        int currLevel = getCurrUserMinLevel(loginNo);
        if (currLevel >= rolePO.getLevels()) {
            throw new ServerException("没有权限做此操作，角色级别必须大于 " + currLevel);
        }
        Role role = new Role();
        role.setAppid(rolePO.getAppid());
        return doSave(role, rolePO);
    }

    @Transactional
    public void doDelete(String loginNo, List<String> idList) throws ServerException {
        int currLevel = getCurrUserMinLevel(loginNo);
        List<Role> roleList = roleRepository.findAllById(idList);
        for (Role role : roleList) {
            if (currLevel >= role.getLevels()) {
                throw new ServerException("没有权限做此操作，请联系系统管理员");
            }
        }
        roleRepository.deleteByIdIn(idList);
    }

    @Transactional
    public Role doUpdate(String loginNo, RolePO rolePO) throws ServerException {
        int currLevel = getCurrUserMinLevel(loginNo);
        if (currLevel > 0 && currLevel >= rolePO.getLevels()) {
            throw new ServerException("没有权限做此操作，角色级别必须大于 " + currLevel);
        }
        Optional<Role> roleOptional = roleRepository.findById(rolePO.getId());
        if (roleOptional.isEmpty()) {
            throw new ServerException("找不到角色信息");
        }
        Role role = roleOptional.get();
        if (currLevel > 0 && currLevel >= role.getLevels()) {
            throw new ServerException("没有权限做此操作，请联系系统管理员");
        }
        return doSave(role, rolePO);
    }

    public RoleVO getRoleInfo(String roleId) throws ServerException {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        if (roleOptional.isEmpty()) {
            throw new ServerException("找不到角色信息");
        }
        Role role = roleOptional.get();
        RoleVO roleVO = new RoleVO();
        roleVO.setId(role.getId());
        roleVO.setAppid(role.getAppid());
        roleVO.setCode(role.getCode());
        roleVO.setLevels(role.getLevels());
        roleVO.setName(role.getName());
        roleVO.setSort(role.getSort());
        roleVO.setUserIds(role.getUserSet().stream().map(User::getId).collect(Collectors.toList()));
        roleVO.setMenuIds(role.getMenuSet().stream().map(Menu::getId).collect(Collectors.toList()));
        roleVO.setModuleFuncIds(role.getModuleFuncSet().stream().map(ModuleFunc::getId).collect(Collectors.toList()));
        return roleVO;
    }
}
