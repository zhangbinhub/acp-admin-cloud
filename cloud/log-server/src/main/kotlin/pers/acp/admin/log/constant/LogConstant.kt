package pers.acp.admin.log.constant

/**
 * @author zhang by 22/11/2019
 * @since JDK 11
 */
object LogConstant {
    /**
     * 登录统计最大月数
     */
    const val LOGIN_LOG_STATISTICS_MAX_MONTH = 3
    /**
     * 路由日志：最大查询原记录次数
     */
    const val ROUTE_LOG_QUERY_MAX_NUMBER = 300
    /**
     * 路由日志：每次查询原记录的间隔时间，单位毫秒
     */
    const val ROUTE_LOG_QUERY_INTERVAL_TIME: Long = 1000
}