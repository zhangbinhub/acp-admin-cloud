package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.RuntimeConfig;
import pers.acp.admin.oauth.po.RuntimePO;
import pers.acp.admin.oauth.producer.instance.UpdateConfigProducer;
import pers.acp.admin.oauth.repo.RuntimeConfigRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class RuntimeConfigDomain extends OauthBaseDomain {

    private final ConcurrentHashMap<String, RuntimeConfig> runtimeConfigConcurrentHashMap = new ConcurrentHashMap<>();

    private final RuntimeConfigRepository runtimeConfigRepository;

    private final UpdateConfigProducer updateConfigProducer;

    @Autowired
    public RuntimeConfigDomain(UserRepository userRepository, RuntimeConfigRepository runtimeConfigRepository, UpdateConfigProducer updateConfigProducer) {
        super(userRepository);
        this.runtimeConfigRepository = runtimeConfigRepository;
        this.updateConfigProducer = updateConfigProducer;
    }

    @PostConstruct
    public void loadRuntimeConfig() {
        synchronized (this) {
            runtimeConfigConcurrentHashMap.clear();
            runtimeConfigRepository.findAll().forEach(runtimeConfig -> runtimeConfigConcurrentHashMap.put(runtimeConfig.getName(), runtimeConfig));
        }
    }

    public RuntimeConfig findByName(String name) {
        return runtimeConfigConcurrentHashMap.get(name);
    }

    @Transactional
    public RuntimeConfig doCreate(RuntimePO runtimePO) throws ServerException {
        Optional<RuntimeConfig> runtimeConfigOptional = runtimeConfigRepository.findByName(runtimePO.getName());
        if (runtimeConfigOptional.isPresent()) {
            throw new ServerException("参数信息已存在");
        }
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        runtimeConfig.setName(runtimePO.getName());
        runtimeConfig.setValue(runtimePO.getValue());
        runtimeConfig.setConfigDes(runtimePO.getConfigDes());
        runtimeConfig.setEnabled(runtimePO.getEnabled());
        runtimeConfig.setCovert(true);
        runtimeConfig = runtimeConfigRepository.save(runtimeConfig);
        updateConfigProducer.doNotifyUpdateRuntime();
        return runtimeConfig;
    }

    @Transactional
    public RuntimeConfig doUpdate(RuntimePO runtimePO) throws ServerException {
        Optional<RuntimeConfig> runtimeConfigOptional = runtimeConfigRepository.findById(runtimePO.getId());
        if (runtimeConfigOptional.isEmpty()) {
            throw new ServerException("找不到参数信息");
        }
        RuntimeConfig runtimeConfig = runtimeConfigOptional.get();
        runtimeConfig.setValue(runtimePO.getValue());
        runtimeConfig.setEnabled(runtimePO.getEnabled());
        runtimeConfig.setConfigDes(runtimePO.getConfigDes());
        runtimeConfig = runtimeConfigRepository.save(runtimeConfig);
        updateConfigProducer.doNotifyUpdateRuntime();
        return runtimeConfig;
    }

    @Transactional
    public void doDelete(List<String> idList) {
        runtimeConfigRepository.deleteByIdInAndCovert(idList, true);
        updateConfigProducer.doNotifyUpdateRuntime();
    }

    public Page<RuntimeConfig> doQuery(RuntimePO runtimePO) {
        return runtimeConfigRepository.findAll((Specification<RuntimeConfig>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!CommonTools.isNullStr(runtimePO.getName())) {
                predicateList.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + runtimePO.getName() + "%"));
            }
            if (!CommonTools.isNullStr(runtimePO.getValue())) {
                predicateList.add(criteriaBuilder.like(root.get("value").as(String.class), "%" + runtimePO.getValue() + "%"));
            }
            if (runtimePO.getEnabled() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("enabled"), runtimePO.getEnabled()));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
        }, buildPageRequest(runtimePO.getQueryParam()));
    }

}
