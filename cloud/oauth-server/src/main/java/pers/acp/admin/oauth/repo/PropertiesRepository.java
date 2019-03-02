package pers.acp.admin.oauth.repo;

import pers.acp.admin.oauth.base.OauthBaseRepository;
import pers.acp.admin.oauth.entity.Properties;

import java.util.List;
import java.util.Optional;

/**
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
public interface PropertiesRepository extends OauthBaseRepository<Properties, String> {

    /**
     * 定位一个可用的配置项
     *
     * @param application 服务名
     * @param profile     配置项
     * @param label       标签
     * @param key         键
     * @param id          ID
     * @return 服务配置
     */
    Optional<Properties> findByConfigApplicationAndConfigProfileAndConfigLabelAndConfigKeyAndEnabledAndIdNot(String application, String profile, String label, String key, boolean enabled, String id);

    /**
     * 删除服务配置
     *
     * @param idList  id列表
     * @param enabled 启用状态
     */
    void deleteByIdInAndEnabled(List<String> idList, boolean enabled);

}
