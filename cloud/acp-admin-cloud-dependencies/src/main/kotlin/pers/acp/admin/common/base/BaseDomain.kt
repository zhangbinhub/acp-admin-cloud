package pers.acp.admin.common.base

import io.github.zhangbinhub.acp.core.CommonTools
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pers.acp.admin.common.po.QueryParam

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
abstract class BaseDomain {

    protected fun buildPageRequest(queryParam: QueryParam): PageRequest =
        if (CommonTools.isNullStr(queryParam.orderName) || CommonTools.isNullStr(queryParam.orderCommand)) {
            PageRequest.of(queryParam.currPage!! - 1, queryParam.pageSize!!)
        } else {
            var direction: Sort.Direction = Sort.Direction.ASC
            if (queryParam.orderCommand!!.startsWith("desc", ignoreCase = true)) {
                direction = Sort.Direction.DESC
            }
            PageRequest.of(
                queryParam.currPage!! - 1,
                queryParam.pageSize!!,
                direction,
                *queryParam.orderName!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            )
        }

}
