package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.BaseDomain;
import pers.acp.admin.oauth.entity.RuntimeConfig;
import pers.acp.admin.oauth.po.ParamPO;
import pers.acp.admin.oauth.repo.RuntimeConfigRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class RuntimeConfigDomain extends BaseDomain {

    private final RuntimeConfigRepository runtimeConfigRepository;

    @Autowired
    public RuntimeConfigDomain(UserRepository userRepository, RuntimeConfigRepository runtimeConfigRepository) {
        super(userRepository);
        this.runtimeConfigRepository = runtimeConfigRepository;
    }

    public RuntimeConfig findByName(String name) {
        return runtimeConfigRepository.findByName(name).orElse(null);
    }

    @Transactional
    public RuntimeConfig doCreate(ParamPO paramPO) throws ServerException {
        Optional<RuntimeConfig> runtimeConfigOptional = runtimeConfigRepository.findByName(paramPO.getName());
        if (runtimeConfigOptional.isPresent()) {
            throw new ServerException("参数信息已存在");
        }
        RuntimeConfig runtimeConfig = new RuntimeConfig();
        runtimeConfig.setName(paramPO.getName());
        runtimeConfig.setValue(paramPO.getValue());
        runtimeConfig.setConfigDes(paramPO.getConfigDes());
        runtimeConfig.setEnabled(paramPO.getEnabled());
        runtimeConfig.setCovert(true);
        return runtimeConfigRepository.save(runtimeConfig);
    }

    @Transactional
    public RuntimeConfig doUpdate(ParamPO paramPO) throws ServerException {
        Optional<RuntimeConfig> runtimeConfigOptional = runtimeConfigRepository.findById(paramPO.getId());
        if (runtimeConfigOptional.isEmpty()) {
            throw new ServerException("找不到参数信息");
        }
        RuntimeConfig runtimeConfig = runtimeConfigOptional.get();
        runtimeConfig.setValue(paramPO.getValue());
        runtimeConfig.setConfigDes(paramPO.getConfigDes());
        runtimeConfig.setEnabled(paramPO.getEnabled());
        return runtimeConfigRepository.save(runtimeConfig);
    }

    @Transactional
    public void doDelete(List<String> idList) {
        runtimeConfigRepository.deleteByIdInAndCovert(idList, true);
    }

    public Page<RuntimeConfig> doQuery(ParamPO paramPO) {
        return runtimeConfigRepository.findAll((Specification<RuntimeConfig>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!CommonTools.isNullStr(paramPO.getName())) {
                predicateList.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + paramPO.getName() + "%"));
            }
            if (!CommonTools.isNullStr(paramPO.getValue())) {
                predicateList.add(criteriaBuilder.like(root.get("value").as(String.class), "%" + paramPO.getValue() + "%"));
            }
            if (paramPO.getEnabled() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("enabled"), paramPO.getEnabled()));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
        }, buildPageRequest(paramPO.getQueryParam()));
    }

}
