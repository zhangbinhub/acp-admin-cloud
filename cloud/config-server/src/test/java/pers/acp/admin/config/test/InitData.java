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

        Properties properties2 = new Properties();
        properties2.setConfigApplication("log-server");
        properties2.setConfigProfile("prod");
        properties2.setConfigLabel("master");
        properties2.setConfigKey("log-server.max-history-day-number");
        properties2.setConfigValue("180");
        properties2.setConfigDes("日志最大保留天数，默认 180 天");
        properties2.setEnabled(true);
        propertiesRepository.save(properties2);
    }

}
