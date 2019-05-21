package pers.acp.admin.route.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.route.entity.Route;
import pers.acp.admin.route.repo.RouteRepository;
import pers.acp.admin.route.BaseTest;

/**
 * @author zhang by 18/12/2018
 * @since JDK 11
 */
class InitData extends BaseTest {

    @Autowired
    private RouteRepository routeRepository;

    /**
     * 初始化数据，仅可执行一次
     */
    @Test
    @Transactional
    @Rollback(false)
    void doInitAll() {
        initGateWayRoute();
    }

    @Test
    @Transactional
    @Rollback(false)
    void initGateWayRoute() {
        Route route1 = new Route();
        route1.setRouteid("log-server-api");
        route1.setEnabled(true);
        route1.setUri("lb://log-server");
        route1.setPredicates("[\"Path=/api/log/**\"]");
        route1.setFilters("[\"StripPrefix=1\",{\"name\":\"Hystrix\",\"args\":{\"name\":\"GateWayHystrix\",\"fallbackUri\":\"forward:/hystrixhandle\"}}]");
        route1.setRemarks("日志服务接口");
        routeRepository.save(route1);
    }

}
