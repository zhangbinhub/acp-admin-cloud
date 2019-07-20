package pers.acp.admin.common.base

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pers.acp.admin.common.po.QueryParam
import pers.acp.core.CommonTools

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
abstract class BaseDomain {

    protected fun buildPageRequest(queryParam: QueryParam): PageRequest =
            if (CommonTools.isNullStr(queryParam.orderName)) {
                PageRequest.of(queryParam.currPage - 1, queryParam.pageSize)
            } else {
                var direction: Sort.Direction = Sort.Direction.DESC
                if (queryParam.orderCommond.equals("asc", ignoreCase = true)) {
                    direction = Sort.Direction.ASC
                }
                PageRequest.of(queryParam.currPage - 1, queryParam.pageSize, direction, *queryParam.orderName!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            }

}
