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
        String key = "serial_number";
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
            System.out.println("开始循环获取序列号，同时启动10个线程，每次间隔100毫秒，循环60次：");
            for (int t = 0; t < 10; t++) {
                new Thread(() -> {
                    for (int i = 0; i < 60; i++) {
                        long serialNumber = generateSerialNumber.getSerialNumber(key, 5000);
                        System.out.println(CommonTools.getNowTimeString() + " 第" + i + "次获取序列号：" + serialNumber);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
//            System.out.println("开始循环获取序列号，同时启动10个线程，无限循环取，直到程序结束");
//            for (int t = 0; t < 10; t++) {
//                new Thread(() -> {
//                    int i = 0;
//                    while (true) {
//                        long serialNumber = generateSerialNumber.getSerialNumber(key, 5000);
//                        System.out.println(CommonTools.getNowTimeString() + " 第" + (++i) + "次获取序列号：" + serialNumber);
//                    }
//                }).start();
//            }
        }).start();
        Thread.sleep(20000);
    }

}
