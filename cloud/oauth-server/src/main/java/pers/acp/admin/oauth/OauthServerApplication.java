package pers.acp.admin.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import pers.acp.springcloud.common.annotation.AcpCloudOauthServerApplication;

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudOauthServerApplication
@AutoConfigureBefore(BindingServiceConfiguration.class)
@RemoteApplicationEventScan
public class OauthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthServerApplication.class, args);
    }

}
