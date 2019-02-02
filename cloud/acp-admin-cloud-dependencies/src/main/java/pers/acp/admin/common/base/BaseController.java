package pers.acp.admin.common.base;

import java.util.Calendar;
import java.util.Date;

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
    protected Date longToDate(long time) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(new Date(time));
        calendarStart.set(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DATE), 0, 0, 0);
        return calendarStart.getTime();
    }

}