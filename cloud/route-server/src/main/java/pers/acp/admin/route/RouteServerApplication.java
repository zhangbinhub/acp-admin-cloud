package pers.acp.admin.route;

import org.springframework.boot.SpringApplication;
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
public class RouteServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RouteServerApplication.class, args);
    }

}
