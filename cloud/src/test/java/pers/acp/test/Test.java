package pers.acp.test;

/**
 * @author zhang by 15/01/2019
 * @since JDK 11
 */
public class Test {

    public static void main(String[] args) {
        String ss = "/foo/assdaf/fsaf/test/adfsadaaaaaaaaaaaaaaa";
        System.out.println(ss.replaceAll("/foo/(?<segment1>.*)/test/(?<segment2>.*)", "/foo/${segment2}"));
    }

}
