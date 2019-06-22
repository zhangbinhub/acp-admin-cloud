package pers.acp.admin.file;

import org.springframework.boot.SpringApplication;
import pers.acp.spring.cloud.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
public class FileServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

}
