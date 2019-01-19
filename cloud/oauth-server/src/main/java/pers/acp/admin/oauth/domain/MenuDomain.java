package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.Menu;
import pers.acp.admin.oauth.entity.Role;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.MenuRepository;
import pers.acp.admin.oauth.repo.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class MenuDomain extends OauthBaseDomain {

    private final MenuRepository menuRepository;

    @Autowired
    public MenuDomain(UserRepository userRepository, MenuRepository menuRepository) {
        super(userRepository);
        this.menuRepository = menuRepository;
    }

    private void sortMenuList(List<Menu> menuList) {
        menuList.forEach(menu -> {
            if (!menu.getChildren().isEmpty()) {
                sortMenuList(menu.getChildren());
            }
        });
        menuList.sort(Comparator.comparingInt(Menu::getSort));
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
        sortMenuList(result);
        return result;
    }

    public List<Menu> getMenuListByAppId(String appId) {
        List<Menu> result = new ArrayList<>();
        Map<String, Menu> menuMap = menuRepository.findByAppidOrderBySortAsc(appId).stream().collect(Collectors.toMap(Menu::getId, menu -> menu));
        menuMap.forEach((id, menu) -> {
            if (menuMap.containsKey(menu.getParentid())) {
                menuMap.get(menu.getParentid()).getChildren().add(menu);
            } else {
                result.add(menu);
            }
        });
        return result;
    }

}
