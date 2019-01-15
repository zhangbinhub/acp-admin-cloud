package pers.acp.admin.oauth.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.po.QueryParam;
import pers.acp.admin.oauth.entity.User;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.core.CommonTools;

/**
 * @author zhang by 26/12/2018
 * @since JDK 11
 */
@Transactional(readOnly = true)
public class BaseDomain {

    protected final UserRepository userRepository;

    @Autowired
    public BaseDomain(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findCurrUserInfo(String loginNo) {
        return userRepository.findByLoginno(loginNo).orElse(null);
    }

    public User findUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    protected PageRequest buildPageRequest(QueryParam queryParam) {
        if (CommonTools.isNullStr(queryParam.getOrderName())) {
            return PageRequest.of(queryParam.getCurrPage() - 1, queryParam.getPageSize());
        } else {
            Sort.Direction direction = Sort.Direction.DESC;
            if (queryParam.getOrderCommond().equalsIgnoreCase("asc")) {
                direction = Sort.Direction.ASC;
            }
            return PageRequest.of(queryParam.getCurrPage() - 1, queryParam.getPageSize(), direction, CommonTools.toCamel(queryParam.getOrderName()).split(","));
        }
    }

}
