package pers.acp.admin.gateway.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pers.acp.admin.gateway.entity.RouteLog;

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
public interface RouteLogRepository extends JpaSpecificationExecutor<RouteLog>, JpaRepository<RouteLog, String> {
}
