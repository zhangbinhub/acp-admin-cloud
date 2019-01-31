package pers.acp.admin.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pers.acp.admin.log.conf.LogServerCustomerConfiguration;
import pers.acp.core.log.LogFactory;

/**
 * @author zhang by 30/01/2019
 * @since JDK 11
 */
@RestController
public class LogController {

    private final LogFactory log = LogFactory.getInstance(this.getClass());

    private final LogServerCustomerConfiguration logServerCustomerConfiguration;

    @Autowired
    public LogController(LogServerCustomerConfiguration logServerCustomerConfiguration) {
        this.logServerCustomerConfiguration = logServerCustomerConfiguration;
    }

}
