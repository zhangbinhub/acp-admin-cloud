package pers.acp.admin.route.definition

import pers.acp.admin.constant.RouteConstant.ROUTES_DEFINITION_KEY

/**
 * @author zhang by 18/03/2019
 * @since JDK 11
 */
object RouteConstant {
    const val ROUTES_LOCK_KEY = ROUTES_DEFINITION_KEY + "_lock"
    const val ROUTES_LOCK_TIME_OUT: Long = 1000
}
