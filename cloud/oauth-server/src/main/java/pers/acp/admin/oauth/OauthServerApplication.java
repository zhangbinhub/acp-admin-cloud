package pers.acp.admin.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import pers.acp.springcloud.common.annotation.AcpCloudOauthServerApplication;

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudOauthServerApplication
@RemoteApplicationEventScan
public class OauthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthServerApplication.class, args);
    }

}
