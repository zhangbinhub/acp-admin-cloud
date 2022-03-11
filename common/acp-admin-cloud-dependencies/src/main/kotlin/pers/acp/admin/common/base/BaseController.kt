package pers.acp.admin.common.base

import io.github.zhangbinhub.acp.boot.interfaces.LogAdapter
import io.github.zhangbinhub.acp.core.CalendarTools
import org.joda.time.DateTime
import org.springframework.security.oauth2.provider.OAuth2Authentication

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
abstract class BaseController(private val logAdapter: LogAdapter) {

    /**
     * long 转为日期，仅保留年月日
     *
     * @param time 时间
     * @return 日期对象
     */
    protected fun longToDate(time: Long): DateTime {
        val dateTime = CalendarTools.getCalendar(time)
        return dateTime.withMillisOfDay(0)
    }

    /**
     * 校验当前token是否具有指定的所有权限
     * @param user 用户token授权信息
     * @param authenticationList 待校验权限列表，权限列表为空则返回false
     */
    protected fun hasAuthentication(user: OAuth2Authentication, authenticationList: MutableList<String>): Boolean =
        authenticationList.let {
            if (it.isNotEmpty()) {
                it.forEach { authentication ->
                    if (user.authorities.none { item -> item.authority == authentication }) {
                        logAdapter.warn("当前用户【${user.name}】没有权限【$authentication】")
                        return@let false
                    }
                }
                true
            } else {
                logAdapter.warn("当前用户【${user.name}】权限列表为空")
                false
            }
        }

}