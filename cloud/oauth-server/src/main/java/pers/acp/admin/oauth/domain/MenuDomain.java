package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.BaseDomain;
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
public class MenuDomain extends BaseDomain {

    private final MenuRepository menuRepository;

    @Autowired
    public MenuDomain(UserRepository userRepository, MenuRepository menuRepository) {
        super(userRepository);
        this.menuRepository = menuRepository;
    }

    @Transactional(readOnly = true)
    public List<Menu> getMenuList(String loginNo) {
        List<Menu> result = new ArrayList<>();
        User user = findCurrUserInfo(loginNo);
        if (user != null) {
            Set<String> menuIds = new HashSet<>();
            Map<String, Menu> menuMap = user.getRoleSet().stream()
                    .map(Role::getMenuSet)
                    .flatMap(Collection::parallelStream)
                    .filter(menu -> {
                        if (menuIds.contains(menu.getId())) {
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
                } else {
                    result.add(menu);
                }
            });
        }
        sortMenuList(result);
        return result;
    }

    private void sortMenuList(List<Menu> menuList) {
        menuList.forEach(menu -> {
            if (!menu.getChildren().isEmpty()) {
                sortMenuList(menu.getChildren());
            }
        });
        menuList.sort(Comparator.comparingInt(Menu::getSort));
    }

}
