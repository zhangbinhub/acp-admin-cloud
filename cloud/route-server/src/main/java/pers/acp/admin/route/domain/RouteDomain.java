package pers.acp.admin.route.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.base.BaseDomain;
import pers.acp.admin.common.lock.DistributedLock;
import pers.acp.admin.route.definition.FilterDefinition;
import pers.acp.admin.route.definition.PredicateDefinition;
import pers.acp.admin.route.definition.RouteConstant;
import pers.acp.admin.route.definition.RouteDefinition;
import pers.acp.admin.route.entity.Route;
import pers.acp.admin.route.entity.RouteLog;
import pers.acp.admin.route.po.RouteLogPO;
import pers.acp.admin.route.po.RoutePO;
import pers.acp.admin.route.repo.RouteLogRepository;
import pers.acp.admin.route.repo.RouteRepository;
import pers.acp.core.CommonTools;
import pers.acp.springboot.core.exceptions.ServerException;
import pers.acp.springcloud.common.log.LogInstance;

import javax.persistence.criteria.Predicate;
import java.net.URI;
import java.util.*;

/**
 * @author zhang by 01/03/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
public class RouteDomain extends BaseDomain {

    private final LogInstance logInstance;

    private final RouteRepository routeRepository;

    private final RouteLogRepository routeLogRepository;

    private final RedisTemplate<Object, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    private final DistributedLock distributedLock;

    @Autowired
    public RouteDomain(LogInstance logInstance, RouteRepository routeRepository, RouteLogRepository routeLogRepository, RedisTemplate<Object, Object> redisTemplate, ObjectMapper objectMapper, DistributedLock distributedLock) {
        this.logInstance = logInstance;
        this.routeRepository = routeRepository;
        this.routeLogRepository = routeLogRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.distributedLock = distributedLock;
    }

    private Route doSave(Route route, RoutePO routePO) {
        route.setUri(routePO.getUri());
        route.setRouteId(routePO.getRouteId());
        route.setPredicates(routePO.getPredicates());
        route.setFilters(routePO.getFilters());
        route.setEnabled(routePO.getEnabled());
        route.setOrderNum(routePO.getOrderNum());
        route.setRemarks(routePO.getRemarks());
        return routeRepository.save(route);
    }

    @Transactional
    public Route doCreate(RoutePO routePO) {
        return doSave(new Route(), routePO);
    }

    @Transactional
    public Route doUpdate(RoutePO routePO) throws ServerException {
        Optional<Route> gateWayRouteOptional = routeRepository.findById(routePO.getId());
        if (gateWayRouteOptional.isEmpty()) {
            throw new ServerException("找不到路由信息");
        }
        Route route = gateWayRouteOptional.get();
        return doSave(route, routePO);
    }

    @Transactional
    public void doDelete(List<String> idList) {
        routeRepository.deleteByIdInAndEnabled(idList, false);
    }

    public Page<Route> doQuery(RoutePO routePO) {
        return routeRepository.findAll((Specification<Route>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (routePO.getEnabled() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("enabled"), routePO.getEnabled()));
            }
            if (!CommonTools.isNullStr(routePO.getRouteId())) {
                predicateList.add(criteriaBuilder.like(root.get("routeId").as(String.class), "%" + routePO.getRouteId() + "%"));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
        }, buildPageRequest(routePO.getQueryParam()));
    }

    public Page<RouteLog> doQueryLog(RouteLogPO routeLogPO) {
        return routeLogRepository.findAll((Specification<RouteLog>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!CommonTools.isNullStr(routeLogPO.getRemoteIp())) {
                predicateList.add(criteriaBuilder.like(root.get("remoteIp").as(String.class), "%" + routeLogPO.getRemoteIp() + "%"));
            }
            if (!CommonTools.isNullStr(routeLogPO.getGatewayIp())) {
                predicateList.add(criteriaBuilder.like(root.get("gatewayIp").as(String.class), "%" + routeLogPO.getGatewayIp() + "%"));
            }
            if (!CommonTools.isNullStr(routeLogPO.getPath())) {
                predicateList.add(criteriaBuilder.like(root.get("path").as(String.class), "%" + routeLogPO.getPath() + "%"));
            }
            if (!CommonTools.isNullStr(routeLogPO.getServerId())) {
                predicateList.add(criteriaBuilder.like(root.get("serverId").as(String.class), "%" + routeLogPO.getServerId() + "%"));
            }
            if (routeLogPO.getStartTime() != null) {
                predicateList.add(criteriaBuilder.ge(root.get("requestTime").as(Long.class), routeLogPO.getStartTime()));
            }
            if (routeLogPO.getEndTime() != null) {
                predicateList.add(criteriaBuilder.le(root.get("requestTime").as(Long.class), routeLogPO.getEndTime()));
            }
            if (routeLogPO.getResponseStatus() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("responseStatus").as(Long.class), routeLogPO.getResponseStatus()));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
        }, buildPageRequest(routeLogPO.getQueryParam()));
    }

    @Transactional
    public void doRefresh() throws ServerException {
        List<Route> routeList = routeRepository.findAllByEnabled(true);
        logInstance.info("查询到启用的路由信息共 " + routeList.size() + " 条");
        try {
            String uuid = CommonTools.getUuid();
            if (distributedLock.getLock(RouteConstant.ROUTES_LOCK_KEY, uuid, 30000)) {
                redisTemplate.delete(RouteConstant.ROUTES_DEFINITION_KEY);
                logInstance.info("清理 Redis 缓存完成");
                for (Route route : routeList) {
                    RouteDefinition routeDefinition = new RouteDefinition();
                    routeDefinition.setId(route.getRouteId());
                    routeDefinition.setUri(new URI(route.getUri()));
                    routeDefinition.setOrder(route.getOrderNum());
                    routeDefinition.setPredicates(objectMapper.readValue(route.getPredicates(), TypeFactory.defaultInstance().constructCollectionLikeType(List.class, PredicateDefinition.class)));
                    routeDefinition.setFilters(objectMapper.readValue(route.getFilters(), TypeFactory.defaultInstance().constructCollectionLikeType(List.class, FilterDefinition.class)));
                    redisTemplate.opsForList().rightPush(RouteConstant.ROUTES_DEFINITION_KEY, objectMapper.writeValueAsBytes(routeDefinition));
                }
                logInstance.info("路由信息更新至 Redis，共 " + routeList.size() + " 条");
                distributedLock.releaseLock(RouteConstant.ROUTES_LOCK_KEY, uuid);
            } else {
                throw new ServerException("系统正在进行路由信息更新，请稍后重试");
            }
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }

}
