package pers.acp.admin.route.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.data.domain.Page
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.base.BaseDomain
import pers.acp.admin.constant.RouteConstant.ROUTES_DEFINITION_KEY
import pers.acp.admin.route.definition.FilterDefinition
import pers.acp.admin.route.definition.PredicateDefinition
import pers.acp.admin.route.definition.RouteConstant
import pers.acp.admin.route.definition.RouteDefinition
import pers.acp.admin.route.entity.Route
import pers.acp.admin.route.po.RoutePo
import pers.acp.admin.route.po.RouteQueryPo
import pers.acp.admin.route.repo.RouteRepository
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException
import pers.acp.spring.boot.interfaces.LogAdapter
import pers.acp.spring.cloud.lock.DistributedLock

import javax.persistence.criteria.Predicate
import java.net.URI

/**
 * @author zhang by 01/03/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class RouteDomain @Autowired
constructor(private val logAdapter: LogAdapter,
            private val routeRepository: RouteRepository,
            private val redisTemplate: RedisTemplate<Any, Any>,
            private val objectMapper: ObjectMapper,
            private val distributedLock: DistributedLock) : BaseDomain() {

    private fun doSave(route: Route, routePo: RoutePo): Route =
            route.copy(
                    uri = routePo.uri,
                    routeId = routePo.routeId,
                    predicates = routePo.predicates!!,
                    filters = routePo.filters,
                    metadata = routePo.metadata,
                    enabled = routePo.enabled ?: false,
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

    fun doQuery(routeQueryPo: RouteQueryPo): Page<Route> =
            routeRepository.findAll({ root, _, criteriaBuilder ->
                val predicateList: MutableList<Predicate> = mutableListOf()
                if (routeQueryPo.enabled != null) {
                    predicateList.add(criteriaBuilder.equal(root.get<Any>("enabled"), routeQueryPo.enabled))
                }
                if (!CommonTools.isNullStr(routeQueryPo.routeId)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("routeId").`as`(String::class.java), "%" + routeQueryPo.routeId + "%"))
                }
                criteriaBuilder.and(*predicateList.toTypedArray())
            }, buildPageRequest(routeQueryPo.queryParam!!))

    @Transactional
    @Throws(ServerException::class)
    fun doRefresh() {
        val routeList = routeRepository.findAllByEnabled(true)
        logAdapter.info("查询到启用的路由信息共 " + routeList.size + " 条")
        try {
            val uuid = CommonTools.getUuid()
            if (distributedLock.getLock(RouteConstant.ROUTES_LOCK_KEY, uuid, RouteConstant.ROUTES_LOCK_TIME_OUT)) {
                redisTemplate.delete(ROUTES_DEFINITION_KEY)
                logAdapter.info("清理 Redis 缓存完成")
                for (route in routeList) {
                    val routeDefinition = RouteDefinition()
                    routeDefinition.id = route.routeId!!
                    routeDefinition.uri = URI(route.uri!!)
                    routeDefinition.order = route.orderNum
                    routeDefinition.predicates = objectMapper.readValue(route.predicates, TypeFactory.defaultInstance().constructCollectionLikeType(MutableList::class.java, PredicateDefinition::class.java))
                    routeDefinition.filters = if (!CommonTools.isNullStr(route.filters)) {
                        objectMapper.readValue<MutableList<FilterDefinition>>(route.filters, TypeFactory.defaultInstance().constructCollectionLikeType(MutableList::class.java, FilterDefinition::class.java))
                    } else {
                        mutableListOf()
                    }
                    routeDefinition.metadata = if (!CommonTools.isNullStr(route.metadata)) {
                        objectMapper.readValue<MutableMap<String, Any>>(route.metadata, TypeFactory.defaultInstance().constructMapLikeType(MutableMap::class.java, String::class.java, Any::class.java))
                    } else {
                        mutableMapOf()
                    }
                    redisTemplate.opsForList().rightPush(ROUTES_DEFINITION_KEY, objectMapper.writeValueAsBytes(routeDefinition))
                }
                logAdapter.info("路由信息更新至 Redis，共 " + routeList.size + " 条")
                distributedLock.releaseLock(RouteConstant.ROUTES_LOCK_KEY, uuid)
            } else {
                throw ServerException("系统正在进行路由信息更新，请稍后重试")
            }
        } catch (e: Exception) {
            throw ServerException(e.message)
        }

    }

}
