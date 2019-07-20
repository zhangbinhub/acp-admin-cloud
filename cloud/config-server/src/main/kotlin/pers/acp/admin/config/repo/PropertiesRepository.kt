package pers.acp.admin.config.repo

import org.springframework.data.repository.NoRepositoryBean
import pers.acp.admin.common.base.BaseRepository
import pers.acp.admin.config.entity.Properties
import java.util.Optional

/**
 * @author zhang by 28/02/2019
 * @since JDK 11
 */
interface PropertiesRepository : BaseRepository<Properties, String> {

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
    fun findByConfigApplicationAndConfigProfileAndConfigLabelAndConfigKeyAndEnabledAndIdNot(application: String, profile: String, label: String, key: String, enabled: Boolean, id: String): Optional<Properties>

    /**
     * 删除服务配置
     *
     * @param idList  id列表
     * @param enabled 启用状态
     */
    fun deleteByIdInAndEnabled(idList: List<String>, enabled: Boolean)

}
