package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.Menu;
import pers.acp.admin.oauth.entity.Role;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.po.MenuPO;
import pers.acp.admin.oauth.repo.MenuRepository;
import pers.acp.admin.oauth.repo.RoleRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.admin.oauth.vo.MenuVO;
import pers.acp.springboot.core.exceptions.ServerException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class MenuDomain extends OauthBaseDomain {

    private final RoleRepository roleRepository;

    private final MenuRepository menuRepository;

    @Autowired
    public MenuDomain(UserRepository userRepository, RoleRepository roleRepository, MenuRepository menuRepository) {
        super(userRepository);
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
    }

    private List<Menu> sortMenuList(List<Menu> menuList) {
        menuList.forEach(menu -> {
            if (!menu.getChildren().isEmpty()) {
                sortMenuList(menu.getChildren());
            }
        });
        menuList.sort(Comparator.comparingInt(Menu::getSort));
        return menuList;
    }

    public List<Menu> getMenuList(String appId, String loginNo) {
        List<Menu> result = new ArrayList<>();
        User user = findCurrUserInfo(loginNo);
        if (user != null) {
            Set<String> menuIds = new HashSet<>();
            Map<String, Menu> menuMap = user.getRoleSet().stream()
                    .filter(role -> role.getAppid().equals(appId))
                    .map(Role::getMenuSet)
                    .flatMap(Collection::parallelStream)
                    .filter(menu -> {
                        if (menuIds.contains(menu.getId()) || !menu.isEnabled()) {
                            return false;
                        } else {
                            menuIds.add(menu.getId());
                            return true;
                        }
                    })
                    .collect(Collectors.toMap(Menu::getId, menu -> menu));
            menuMap.forEach((id, menu) -> {
                if (menuMap.containsKey(menu.getParentid())) {
                    menuMap.get(menu.getParentid()).getChildren().add(menu);
                } else if (menu.getParentid().equals(menu.getAppid())) {
                    result.add(menu);
                }
            });
        }
        return sortMenuList(result);
    }

    public List<Menu> getMenuListByAppId(String appId) {
        return sortMenuList(formatToTreeList(menuRepository.findByAppid(appId).stream().collect(Collectors.toMap(Menu::getId, menu -> menu))));
    }

    public List<Menu> getAllMenuList() {
        return sortMenuList(formatToTreeList(menuRepository.findAll().stream().collect(Collectors.toMap(Menu::getId, menu -> menu))));
    }

    private Menu doSave(Menu menu, MenuPO menuPO) {
        menu.setPath(menuPO.getPath());
        menu.setEnabled(menuPO.isEnabled());
        menu.setIconType(menuPO.getIconType());
        menu.setName(menuPO.getName());
        menu.setParentid(menuPO.getParentid());
        menu.setOpentype(menuPO.getOpentype());
        menu.setSort(menuPO.getSort());
        menu.setRoleSet(new HashSet<>(roleRepository.findAllById(menuPO.getRoleIds())));
        menu.setCovert(true);
        return menuRepository.save(menu);
    }

    @Transactional
    public Menu doCreate(MenuPO menuPO) {
        Menu menu = new Menu();
        menu.setAppid(menuPO.getAppid());
        return doSave(menu, menuPO);
    }

    @Transactional
    public void doDelete(List<String> idList) {
        menuRepository.deleteByIdInAndCovert(idList, true);
    }

    @Transactional
    public Menu doUpdate(MenuPO menuPO) throws ServerException {
        Optional<Menu> menuOptional = menuRepository.findById(menuPO.getId());
        if (menuOptional.isEmpty()) {
            throw new ServerException("找不到菜单信息");
        }
        Menu menu = menuOptional.get();
        return doSave(menu, menuPO);
    }

    public MenuVO getMenuInfo(String menuId) throws ServerException {
        Optional<Menu> menuOptional = menuRepository.findById(menuId);
        if (menuOptional.isEmpty()) {
            throw new ServerException("找不到菜单信息");
        }
        Menu menu = menuOptional.get();
        MenuVO menuVO = new MenuVO();
        menuVO.setId(menu.getId());
        menuVO.setAppid(menu.getAppid());
        menuVO.setEnabled(menu.isEnabled());
        menuVO.setIconType(menu.getIconType());
        menuVO.setName(menu.getName());
        menuVO.setOpentype(menu.getOpentype());
        menuVO.setParentid(menu.getParentid());
        menuVO.setPath(menu.getPath());
        menuVO.setSort(menu.getSort());
        menuVO.setRoleIds(menu.getRoleSet().stream().map(Role::getId).collect(Collectors.toList()));
        return menuVO;
    }

}
