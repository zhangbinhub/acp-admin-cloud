package pers.acp.admin.config;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication;

/**
 * @author zhang by 27/02/2019
 * @since JDK 11
 */
@AcpCloudAtomApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}
