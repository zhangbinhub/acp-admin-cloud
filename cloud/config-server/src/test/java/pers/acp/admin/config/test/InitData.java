package pers.acp.admin.config.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.config.BaseTest;
import pers.acp.admin.config.entity.Properties;
import pers.acp.admin.config.repo.PropertiesRepository;

/**
 * @author zhang by 18/12/2018
 * @since JDK 11
 */
class InitData extends BaseTest {

    @Autowired
    private PropertiesRepository propertiesRepository;

    /**
     * 初始化数据，仅可执行一次
     */
    @Test
    @Transactional
    @Rollback(false)
    void doInitAll() {
        initProperties();
    }

    @Test
    @Transactional
    @Rollback(false)
    void initProperties() {
        Properties properties = new Properties();
        properties.setConfigApplication("log-server");
        properties.setConfigProfile("dev");
        properties.setConfigLabel("master");
        properties.setConfigKey("log-server.max-history-day-number");
        properties.setConfigValue("5");
        properties.setConfigDes("日志最大保留天数，默认 180 天");
        properties.setEnabled(true);
        propertiesRepository.save(properties);

        Properties properties1 = new Properties();
        properties1.setConfigApplication("log-server");
        properties1.setConfigProfile("dev");
        properties1.setConfigLabel("master");
        properties1.setConfigKey("log-server.route-log");
        properties1.setConfigValue("true");
        properties1.setConfigDes("是否开启路由日志");
        properties1.setEnabled(true);
        propertiesRepository.save(properties1);

        Properties properties2 = new Properties();
        properties2.setConfigApplication("log-server");
        properties2.setConfigProfile("prod");
        properties2.setConfigLabel("master");
        properties2.setConfigKey("log-server.max-history-day-number");
        properties2.setConfigValue("180");
        properties2.setConfigDes("日志最大保留天数，默认 180 天");
        properties2.setEnabled(true);
        propertiesRepository.save(properties2);

        Properties properties3 = new Properties();
        properties3.setConfigApplication("log-server");
        properties3.setConfigProfile("prod");
        properties3.setConfigLabel("master");
        properties3.setConfigKey("log-server.route-log");
        properties3.setConfigValue("true");
        properties3.setConfigDes("是否开启路由日志");
        properties3.setEnabled(true);
        propertiesRepository.save(properties3);

        Properties properties4 = new Properties();
        properties4.setConfigApplication("log-server");
        properties4.setConfigProfile("dev");
        properties4.setConfigLabel("master");
        properties4.setConfigKey("log-server.operate-log");
        properties4.setConfigValue("true");
        properties4.setConfigDes("是否开启操作日志");
        properties4.setEnabled(true);
        propertiesRepository.save(properties4);

        Properties properties5 = new Properties();
        properties5.setConfigApplication("log-server");
        properties5.setConfigProfile("prod");
        properties5.setConfigLabel("master");
        properties5.setConfigKey("log-server.operate-log");
        properties5.setConfigValue("true");
        properties5.setConfigDes("是否开启操作日志");
        properties5.setEnabled(true);
        propertiesRepository.save(properties5);
    }

}
