package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.Properties;
import pers.acp.admin.oauth.po.PropertiesPO;
import pers.acp.admin.oauth.repo.PropertiesRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhang by 01/03/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class PropertiesDomain extends OauthBaseDomain {

    private final PropertiesRepository propertiesRepository;

    @Autowired
    public PropertiesDomain(UserRepository userRepository, PropertiesRepository propertiesRepository) {
        super(userRepository);
        this.propertiesRepository = propertiesRepository;
    }

    /**
     * 检查相同服务名、配置项、标签、键且已启用的服务配置是否已存在
     *
     * @param propertiesPO 参数
     * @throws ServerException 异常信息
     */
    private void checkEnabledExist(PropertiesPO propertiesPO) throws ServerException {
        if (propertiesPO.getEnabled()) {
            Optional<Properties> propertiesOptional = propertiesRepository.findByConfigApplicationAndConfigProfileAndConfigLabelAndConfigKeyAndEnabledAndIdNot(
                    propertiesPO.getConfigApplication(), propertiesPO.getConfigProfile(), propertiesPO.getConfigLabel(), propertiesPO.getConfigKey(),
                    propertiesPO.getEnabled(), propertiesPO.getId());
            if (propertiesOptional.isPresent()) {
                throw new ServerException("有效的服务配置已存在，请重新提交或改为\"不可用\"");
            }
        }
    }

    private Properties doSave(Properties properties, PropertiesPO propertiesPO) {
        properties.setConfigApplication(propertiesPO.getConfigApplication());
        properties.setConfigProfile(propertiesPO.getConfigProfile());
        properties.setConfigLabel(propertiesPO.getConfigLabel());
        properties.setConfigKey(propertiesPO.getConfigKey());
        properties.setConfigValue(propertiesPO.getConfigValue());
        properties.setConfigDes(propertiesPO.getConfigDes());
        properties.setEnabled(propertiesPO.getEnabled());
        return propertiesRepository.save(properties);
    }

    @Transactional
    public Properties doCreate(PropertiesPO propertiesPO) throws ServerException {
        propertiesPO.setId("");
        checkEnabledExist(propertiesPO);
        return doSave(new Properties(), propertiesPO);
    }

    @Transactional
    public Properties doUpdate(PropertiesPO propertiesPO) throws ServerException {
        Optional<Properties> propertiesOptional = propertiesRepository.findById(propertiesPO.getId());
        if (propertiesOptional.isEmpty()) {
            throw new ServerException("找不到服务配置信息");
        }
        checkEnabledExist(propertiesPO);
        Properties properties = propertiesOptional.get();
        return doSave(properties, propertiesPO);
    }

    @Transactional
    public void doDelete(List<String> idList) {
        propertiesRepository.deleteByIdInAndEnabled(idList, false);
    }

    public Page<Properties> doQuery(PropertiesPO propertiesPO) {
        return propertiesRepository.findAll((Specification<Properties>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (propertiesPO.getEnabled() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("enabled"), propertiesPO.getEnabled()));
            }
            if (!CommonTools.isNullStr(propertiesPO.getConfigApplication())) {
                predicateList.add(criteriaBuilder.like(root.get("configApplication").as(String.class), "%" + propertiesPO.getConfigApplication() + "%"));
            }
            if (!CommonTools.isNullStr(propertiesPO.getConfigProfile())) {
                predicateList.add(criteriaBuilder.like(root.get("configProfile").as(String.class), "%" + propertiesPO.getConfigProfile() + "%"));
            }
            if (!CommonTools.isNullStr(propertiesPO.getConfigLabel())) {
                predicateList.add(criteriaBuilder.like(root.get("configLabel").as(String.class), "%" + propertiesPO.getConfigLabel() + "%"));
            }
            if (!CommonTools.isNullStr(propertiesPO.getConfigKey())) {
                predicateList.add(criteriaBuilder.like(root.get("configKey").as(String.class), "%" + propertiesPO.getConfigKey() + "%"));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
        }, buildPageRequest(propertiesPO.getQueryParam()));
    }

}
