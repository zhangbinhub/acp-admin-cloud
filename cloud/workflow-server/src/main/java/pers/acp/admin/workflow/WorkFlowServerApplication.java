package pers.acp.admin.workflow;

import org.springframework.boot.SpringApplication;
import pers.acp.springcloud.common.annotation.AcpCloudAtomApplication;

/**
 * @author zhangbin by 09/04/2018 16:11
 * @since JDK 11
 */
@AcpCloudAtomApplication
public class WorkFlowServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkFlowServerApplication.class, args);
    }

}
