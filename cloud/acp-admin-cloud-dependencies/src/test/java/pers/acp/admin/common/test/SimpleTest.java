package pers.acp.admin.common.test;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import pers.acp.core.CommonTools;
import pers.acp.core.task.timer.Calculation;
import pers.acp.spring.boot.exceptions.ServerException;

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
class SimpleTest {

    @Test
    void test() {
        String ss = "/foo/assdaf/fsaf/test/adfsadaaaaaaaaaaaaaaa";
        System.out.println(ss.replaceAll("/foo/(?<segment1>.*)/test/(?<segment2>.*)", "/foo/${segment2}"));

        ServerException serverException = new ServerException("错误信息");
        System.out.println(CommonTools.objectToJson(serverException));

        System.out.println("1900-01-01".matches("^(20\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$"));

        DateTime dateTime = CommonTools.getNowDateTime().withTimeAtStartOfDay();
        System.out.println(dateTime.toString(Calculation.DATETIME_FORMAT));
        System.out.println(dateTime.getMillis());
        System.out.println(dateTime.toDate().getTime());
    }

}
