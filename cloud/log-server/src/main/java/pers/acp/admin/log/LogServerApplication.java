package pers.acp.admin.log;

import org.springframework.boot.SpringApplication;
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
public class LogServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogServerApplication.class, args);
    }

}
