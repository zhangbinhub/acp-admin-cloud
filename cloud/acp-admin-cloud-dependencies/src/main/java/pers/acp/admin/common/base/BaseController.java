package pers.acp.admin.common.base;

import org.joda.time.DateTime;
import pers.acp.core.CalendarTools;

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
public abstract class BaseController {

    /**
     * long 转为日期，仅保留年月日
     *
     * @param time 时间
     * @return 日期对象
     */
    protected DateTime longToDate(long time) {
        DateTime dateTime = CalendarTools.getCalendar(time);
        return dateTime.withMillisOfDay(0);
    }

}