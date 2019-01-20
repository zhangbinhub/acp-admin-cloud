package pers.acp.admin.oauth.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.base.BaseDomain;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@Transactional(readOnly = true)
public class OauthBaseDomain extends BaseDomain {

    protected final UserRepository userRepository;

    @Autowired
    public OauthBaseDomain(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findCurrUserInfo(String loginNo) {
        return userRepository.findByLoginno(loginNo).orElse(null);
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
