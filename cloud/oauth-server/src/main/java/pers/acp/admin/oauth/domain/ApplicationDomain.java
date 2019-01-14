package pers.acp.admin.oauth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.oauth.base.BaseDomain;
import pers.acp.admin.oauth.entity.Application;
import pers.acp.admin.oauth.po.ApplicationPO;
import pers.acp.admin.oauth.repo.ApplicationRepository;
import pers.acp.admin.oauth.repo.UserRepository;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
@Service
public class ApplicationDomain extends BaseDomain {

    private final ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationDomain(UserRepository userRepository, ApplicationRepository applicationRepository) {
        super(userRepository);
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public Application doCreate(ApplicationPO applicationPO) {
        Application application = new Application();
        application.setAppname(applicationPO.getAppname());
        application.setSecret(CommonTools.getUuid());
        application.setAccessTokenValiditySeconds(applicationPO.getAccessTokenValiditySeconds());
        application.setRefreshTokenValiditySeconds(applicationPO.getRefreshTokenValiditySeconds());
        application.setCovert(true);
        return applicationRepository.save(application);
    }

    @Transactional
    public Application doUpdate(ApplicationPO applicationPO) throws ServerException {
        Optional<Application> applicaitonOptional = applicationRepository.findById(applicationPO.getId());
        if (applicaitonOptional.isEmpty()) {
            throw new ServerException("找不到信息");
        }
        Application application = applicaitonOptional.get();
        application.setAppname(applicationPO.getAppname());
        application.setAccessTokenValiditySeconds(applicationPO.getAccessTokenValiditySeconds());
        application.setRefreshTokenValiditySeconds(applicationPO.getRefreshTokenValiditySeconds());
        return applicationRepository.save(application);
    }

    @Transactional
    public Application doUpdateSecret(String appId) throws ServerException {
        Optional<Application> applicaitonOptional = applicationRepository.findById(appId);
        if (applicaitonOptional.isEmpty()) {
            throw new ServerException("找不到信息");
        }
        Application application = applicaitonOptional.get();
        application.setSecret(CommonTools.getUuid());
        return applicationRepository.save(application);
    }

    @Transactional
    public void doDelete(List<String> idList) {
        applicationRepository.deleteByIdInAndCovert(idList, true);
    }

    public Page<Application> doQuery(ApplicationPO applicationPO) {
        return applicationRepository.findAll((Specification<Application>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!CommonTools.isNullStr(applicationPO.getAppname())) {
                predicateList.add(criteriaBuilder.like(root.get("appname").as(String.class), "%" + applicationPO.getAppname() + "%"));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
        }, buildPageRequest(applicationPO.getQueryParam()));
    }

}
