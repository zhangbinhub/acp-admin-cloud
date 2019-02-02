package pers.acp.admin.common.base;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pers.acp.admin.common.po.QueryParam;
import pers.acp.core.CommonTools;

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
public abstract class BaseDomain {

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
