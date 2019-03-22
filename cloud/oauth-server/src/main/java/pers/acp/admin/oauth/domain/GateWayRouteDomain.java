package pers.acp.admin.oauth.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.acp.admin.common.lock.DistributedLock;
import pers.acp.admin.oauth.base.OauthBaseDomain;
import pers.acp.admin.oauth.entity.GateWayRoute;
import pers.acp.admin.oauth.entity.route.GateWayFilterDefinition;
import pers.acp.admin.oauth.entity.route.GateWayPredicateDefinition;
import pers.acp.admin.oauth.entity.route.GateWayRouteConstant;
import pers.acp.admin.oauth.entity.route.GateWayRouteDefinition;
import pers.acp.admin.oauth.po.GateWayRoutePO;
import pers.acp.admin.oauth.repo.GateWayRouteRepository;
import pers.acp.admin.oauth.repo.UserRepository;
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
public class GateWayRouteDomain extends OauthBaseDomain {

    private final LogInstance logInstance;

    private final GateWayRouteRepository gateWayRouteRepository;

    private final RedisConnectionFactory redisConnectionFactory;

    private final ObjectMapper objectMapper;

    private final DistributedLock distributedLock;

    @Autowired
    public GateWayRouteDomain(UserRepository userRepository, LogInstance logInstance, GateWayRouteRepository gateWayRouteRepository, RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper, DistributedLock distributedLock) {
        super(userRepository);
        this.logInstance = logInstance;
        this.gateWayRouteRepository = gateWayRouteRepository;
        this.redisConnectionFactory = redisConnectionFactory;
        this.objectMapper = objectMapper;
        this.distributedLock = distributedLock;
    }

    private GateWayRoute doSave(GateWayRoute gateWayRoute, GateWayRoutePO gateWayRoutePO) {
        gateWayRoute.setUri(gateWayRoutePO.getUri());
        gateWayRoute.setRouteid(gateWayRoutePO.getRouteid());
        gateWayRoute.setPredicates(gateWayRoutePO.getPredicates());
        gateWayRoute.setFilters(gateWayRoutePO.getFilters());
        gateWayRoute.setEnabled(gateWayRoutePO.getEnabled());
        gateWayRoute.setOrderNum(gateWayRoutePO.getOrderNum());
        return gateWayRouteRepository.save(gateWayRoute);
    }

    @Transactional
    public GateWayRoute doCreate(GateWayRoutePO gateWayRoutePO) {
        return doSave(new GateWayRoute(), gateWayRoutePO);
    }

    @Transactional
    public GateWayRoute doUpdate(GateWayRoutePO gateWayRoutePO) throws ServerException {
        Optional<GateWayRoute> gateWayRouteOptional = gateWayRouteRepository.findById(gateWayRoutePO.getId());
        if (gateWayRouteOptional.isEmpty()) {
            throw new ServerException("找不到路由信息");
        }
        GateWayRoute gateWayRoute = gateWayRouteOptional.get();
        return doSave(gateWayRoute, gateWayRoutePO);
    }

    @Transactional
    public void doDelete(List<String> idList) {
        gateWayRouteRepository.deleteByIdInAndEnabled(idList, false);
    }

    public Page<GateWayRoute> doQuery(GateWayRoutePO gateWayRoutePO) {
        return gateWayRouteRepository.findAll((Specification<GateWayRoute>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (gateWayRoutePO.getEnabled() != null) {
                predicateList.add(criteriaBuilder.equal(root.get("enabled"), gateWayRoutePO.getEnabled()));
            }
            if (!CommonTools.isNullStr(gateWayRoutePO.getRouteid())) {
                predicateList.add(criteriaBuilder.like(root.get("routeid").as(String.class), "%" + gateWayRoutePO.getRouteid() + "%"));
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[]{}));
        }, buildPageRequest(gateWayRoutePO.getQueryParam()));
    }

    @Transactional
    public void doRefresh() throws ServerException {
        List<GateWayRoute> gateWayRouteList = gateWayRouteRepository.findAllByEnabled(true);
        logInstance.info("查询到启用的路由信息共 " + gateWayRouteList.size() + " 条");
        RedisConnection connection = redisConnectionFactory.getConnection();
        try {
            String uuid = CommonTools.getUuid();
            if (distributedLock.getLock(GateWayRouteConstant.ROUTES_LOCK_KEY, uuid, 30000)) {
                connection.del(GateWayRouteConstant.ROUTES_DEFINITION_KEY.getBytes());
                logInstance.info("清理 Redis 缓存完成");
                List<byte[]> gateWayRouteDefinitionList = new ArrayList<>();
                for (GateWayRoute gateWayRoute : gateWayRouteList) {
                    GateWayRouteDefinition gateWayRouteDefinition = new GateWayRouteDefinition();
                    gateWayRouteDefinition.setId(gateWayRoute.getRouteid());
                    gateWayRouteDefinition.setUri(new URI(gateWayRoute.getUri()));
                    gateWayRouteDefinition.setOrder(gateWayRoute.getOrderNum());
                    gateWayRouteDefinition.setPredicates(objectMapper.readValue(gateWayRoute.getPredicates(), TypeFactory.defaultInstance().constructCollectionLikeType(List.class, GateWayPredicateDefinition.class)));
                    gateWayRouteDefinition.setFilters(objectMapper.readValue(gateWayRoute.getFilters(), TypeFactory.defaultInstance().constructCollectionLikeType(List.class, GateWayFilterDefinition.class)));
                    gateWayRouteDefinitionList.add(objectMapper.writeValueAsBytes(gateWayRouteDefinition));
                }
                connection.lPush(GateWayRouteConstant.ROUTES_DEFINITION_KEY.getBytes(), gateWayRouteDefinitionList.toArray(new byte[][]{}));
                logInstance.info("路由信息更新至 Redis，共 " + gateWayRouteDefinitionList.size() + " 条");
                distributedLock.releaseLock(GateWayRouteConstant.ROUTES_LOCK_KEY, uuid);
            } else {
                throw new ServerException("系统正在进行路由信息更新，请稍后重试");
            }
        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        } finally {
            connection.close();
        }
    }

}
