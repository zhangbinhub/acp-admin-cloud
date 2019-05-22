package pers.acp.admin.route.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.base.BaseDomain;
import pers.acp.admin.route.entity.RouteLog;
import pers.acp.admin.route.repo.RouteLogRepository;

/**
 * @author zhang by 15/05/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class RouteLogDomain extends BaseDomain {

    private final RouteLogRepository routeLogRepository;

    @Autowired
    public RouteLogDomain(RouteLogRepository routeLogRepository) {
        this.routeLogRepository = routeLogRepository;
    }

    @Transactional
    public void doLog(RouteLog routeLog) {
        routeLogRepository.save(routeLog);
    }

}
