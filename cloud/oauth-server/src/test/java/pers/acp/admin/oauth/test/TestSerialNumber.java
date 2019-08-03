package pers.acp.admin.oauth.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pers.acp.admin.common.serialnumber.GenerateSerialNumber;
import pers.acp.admin.oauth.BaseTest;
import pers.acp.core.CommonTools;

/**
 * @author zhang by 03/08/2019
 * @since JDK 11
 */
class TestSerialNumber extends BaseTest {

    @Autowired
    private GenerateSerialNumber generateSerialNumber;

    @Test
    void testGenerateSerialNumber() throws InterruptedException {
        String key = "321";
        long number = generateSerialNumber.getSerialNumber(key, 10000);
        System.out.println(CommonTools.getNowTimeString() + " 第一次获取序列号（超时时间10秒）：" + number);
        new Thread(() -> {
            System.out.println("等待11秒");
            try {
                Thread.sleep(11000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long number1 = generateSerialNumber.getSerialNumber(key, 5000);
            System.out.println(CommonTools.getNowTimeString() + " 第二次获取序列号（超时时间5秒）：" + number1);
            System.out.println("开始循环获取序列号，每次间隔100毫秒，循环60次：");
            for (int i = 0; i < 60; i++) {
                number1 = generateSerialNumber.getSerialNumber(key, 5000);
                System.out.println(CommonTools.getNowTimeString() + " 第" + i + "次获取序列号：" + number1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Thread.sleep(20000);
    }

}
