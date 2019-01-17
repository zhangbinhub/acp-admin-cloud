package pers.acp.admin.common.test;

import org.junit.jupiter.api.Test;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;

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
    }

}
