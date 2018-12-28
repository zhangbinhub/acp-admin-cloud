package pers.acp.admin.atom.common;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhang by 18/12/2018
 * @since JDK 11
 */
@Configuration
@ComponentScan("pers.acp.admin.atom.common")
@ServletComponentScan({"pers.acp.admin.atom.common"})
public class AcpAdminAtomAutoConfiguration {
}
