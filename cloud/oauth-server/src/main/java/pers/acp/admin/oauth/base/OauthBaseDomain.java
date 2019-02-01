package pers.acp.admin.oauth.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.base.BaseDomain;
import pers.acp.admin.common.constant.RoleCode;
import pers.acp.admin.oauth.entity.Role;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@Transactional(readOnly = true)
public abstract class OauthBaseDomain extends BaseDomain {

    protected final UserRepository userRepository;

    @Autowired
    public OauthBaseDomain(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findCurrUserInfo(String loginNo) {
        return userRepository.findByLoginno(loginNo).orElse(null);
    }

    /**
     * 判断指定用户是否是超级管理员
     *
     * @param user 用户对象
     * @return true|false
     */
    protected boolean isAdmin(User user) {
        if (user != null) {
            return user.getRoleSet().stream()
                    .map(Role::getCode)
                    .collect(Collectors.toList())
                    .contains(RoleCode.ADMIN);
        }
        return false;
    }

    /**
     * 获取指定用户所属角色中最高级别
     *
     * @param appId 应用ID
     * @param user  用户对象
     * @return 级别
     */
    protected int getRoleMinLevel(String appId, User user) {
        if (user != null) {
            final int[] level = {Integer.MAX_VALUE};
            user.getRoleSet().stream().filter(role -> role.getAppid().equals(appId)).forEach(role -> {
                if (level[0] > role.getLevels()) {
                    level[0] = role.getLevels();
                }
            });
            return level[0];
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 获取指定用户所属角色中最高级别
     *
     * @param user 用户对象
     * @return 级别
     */
    protected Map<String, Integer> getRoleMinLevel(User user) {
        Map<String, Integer> minMap = new HashMap<>();
        if (user != null) {
            user.getRoleSet().forEach(role -> {
                if (minMap.containsKey(role.getAppid())) {
                    if (minMap.get(role.getAppid()) > role.getLevels()) {
                        minMap.put(role.getAppid(), role.getLevels());
                    }
                } else {
                    minMap.put(role.getAppid(), role.getLevels());
                }
            });
        }
        return minMap;
    }

    @SuppressWarnings("unchecked")
    protected <T extends OauthBaseTreeEntity> List<T> formatToTreeList(Map<String, OauthBaseTreeEntity<T>> map) {
        List<T> result = new ArrayList<>();
        map.forEach((id, item) -> {
            if (map.containsKey(item.getParentid())) {
                map.get(item.getParentid()).getChildren().add((T) item);
            } else {
                result.add((T) item);
            }
        });
        return result;
    }

}
