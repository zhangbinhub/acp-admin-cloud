package pers.acp.springcloud.common;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangbin by 2018-3-14 15:13
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.springcloud.common")
@ServletComponentScan({"pers.acp.springcloud.common"})
public class AcpCloudAutoConfiguration {
}
