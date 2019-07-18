package pers.acp.admin.route.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.data.domain.Page
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.common.lock.DistributedLock
import pers.acp.admin.route.definition.FilterDefinition
import pers.acp.admin.route.definition.PredicateDefinition
import pers.acp.admin.route.definition.RouteConstant
import pers.acp.admin.route.definition.RouteDefinition
import pers.acp.admin.route.entity.Route
import pers.acp.admin.route.entity.RouteLog
import pers.acp.admin.route.po.RouteLogPo
import pers.acp.admin.route.po.RoutePo
import pers.acp.admin.route.repo.RouteLogRepository
import pers.acp.admin.route.repo.RouteRepository
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.cloud.log.LogInstance

import javax.persistence.criteria.Predicate
import java.net.URI
import javax.persistence.metamodel.SingularAttribute

/**
 * @author zhang by 01/03/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class RouteDomain @Autowired
constructor(private val logInstance: LogInstance,
            private val routeRepository: RouteRepository,
            private val routeLogRepository: RouteLogRepository,
            private val redisTemplate: RedisTemplate<Any, Any>,
            private val objectMapper: ObjectMapper,
            private val distributedLock: DistributedLock) : BaseDomain() {

    private fun doSave(route: Route, routePo: RoutePo): Route =
            route.copy(
                    uri = routePo.uri,
                    routeId = routePo.routeId,
                    predicates = routePo.predicates,
                    filters = routePo.filters,
                    enabled = routePo.enabled!!,
                    orderNum = routePo.orderNum,
                    remarks = routePo.remarks
            ).let {
                routeRepository.save(it)
            }

    @Transactional
    fun doCreate(routePo: RoutePo): Route = doSave(Route(), routePo)

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(routePo: RoutePo): Route = doSave(routeRepository.getOne(routePo.id!!), routePo)

    @Transactional
    fun doDelete(idList: List<String>) = routeRepository.deleteByIdInAndEnabled(idList, false)

    fun doQuery(routePo: RoutePo): Page<Route> {
        return routeRepository.findAll({ root, _, criteriaBuilder ->
            val predicateList: MutableList<Predicate> = mutableListOf()
            if (routePo.enabled != null) {
                predicateList.add(criteriaBuilder.equal(root.get(root.model.getAttribute("enabled") as SingularAttribute), routePo.enabled))
            }
            if (!CommonTools.isNullStr(routePo.routeId)) {
                predicateList.add(criteriaBuilder.like(root.get(root.model.getAttribute("routeId") as SingularAttribute).`as`(String::class.java), "%" + routePo.routeId + "%"))
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }, buildPageRequest(routePo.queryParam!!))
    }

    fun doQueryLog(routeLogPO: RouteLogPo): Page<RouteLog> {
        return routeLogRepository.findAll({ root, _, criteriaBuilder ->
            val predicateList: MutableList<Predicate> = mutableListOf()
            if (!CommonTools.isNullStr(routeLogPO.remoteIp)) {
                predicateList.add(criteriaBuilder.like(root.get(root.model.getAttribute("remoteIp") as SingularAttribute).`as`(String::class.java), "%" + routeLogPO.remoteIp + "%"))
            }
            if (!CommonTools.isNullStr(routeLogPO.gatewayIp)) {
                predicateList.add(criteriaBuilder.like(root.get(root.model.getAttribute("gatewayIp") as SingularAttribute).`as`(String::class.java), "%" + routeLogPO.gatewayIp + "%"))
            }
            if (!CommonTools.isNullStr(routeLogPO.path)) {
                predicateList.add(criteriaBuilder.like(root.get(root.model.getAttribute("path") as SingularAttribute).`as`(String::class.java), "%" + routeLogPO.path + "%"))
            }
            if (!CommonTools.isNullStr(routeLogPO.serverId)) {
                predicateList.add(criteriaBuilder.like(root.get(root.model.getAttribute("serverId") as SingularAttribute).`as`(String::class.java), "%" + routeLogPO.serverId + "%"))
            }
            if (routeLogPO.startTime != null) {
                predicateList.add(criteriaBuilder.ge(root.get(root.model.getAttribute("requestTime") as SingularAttribute).`as`(Long::class.java), routeLogPO.startTime))
            }
            if (routeLogPO.endTime != null) {
                predicateList.add(criteriaBuilder.le(root.get(root.model.getAttribute("requestTime") as SingularAttribute).`as`(Long::class.java), routeLogPO.endTime))
            }
            if (routeLogPO.responseStatus != null) {
                predicateList.add(criteriaBuilder.equal(root.get(root.model.getAttribute("responseStatus") as SingularAttribute).`as`(Long::class.java), routeLogPO.responseStatus))
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }, buildPageRequest(routeLogPO.queryParam!!))
    }

    @Transactional
    @Throws(ServerException::class)
    fun doRefresh() {
        val routeList = routeRepository.findAllByEnabled(true)
        logInstance.info("查询到启用的路由信息共 " + routeList.size + " 条")
        try {
            val uuid = CommonTools.getUuid()
            if (distributedLock.getLock(RouteConstant.ROUTES_LOCK_KEY, uuid, 30000)) {
                redisTemplate.delete(RouteConstant.ROUTES_DEFINITION_KEY)
                logInstance.info("清理 Redis 缓存完成")
                for (route in routeList) {
                    val routeDefinition = RouteDefinition()
                    routeDefinition.id = route.routeId!!
                    routeDefinition.uri = URI(route.uri!!)
                    routeDefinition.order = route.orderNum
                    routeDefinition.predicates = objectMapper.readValue(route.predicates, TypeFactory.defaultInstance().constructCollectionLikeType(MutableList::class.java, PredicateDefinition::class.java))
                    routeDefinition.filters = objectMapper.readValue(route.filters, TypeFactory.defaultInstance().constructCollectionLikeType(MutableList::class.java, FilterDefinition::class.java))
                    redisTemplate.opsForList().rightPush(RouteConstant.ROUTES_DEFINITION_KEY, objectMapper.writeValueAsBytes(routeDefinition))
                }
                logInstance.info("路由信息更新至 Redis，共 " + routeList.size + " 条")
                distributedLock.releaseLock(RouteConstant.ROUTES_LOCK_KEY, uuid)
            } else {
                throw ServerException("系统正在进行路由信息更新，请稍后重试")
            }
        } catch (e: Exception) {
            throw ServerException(e.message)
        }

    }

}
