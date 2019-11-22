package pers.acp.admin.oauth.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.RuntimeConfig
import pers.acp.admin.oauth.po.RuntimePo
import pers.acp.admin.oauth.repo.RuntimeConfigRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException

import javax.annotation.PostConstruct
import javax.persistence.criteria.Predicate
import java.util.concurrent.ConcurrentHashMap

/**
 * @author zhang by 11/01/2019
 * @since JDK 11
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(readOnly = true)
class RuntimeConfigDomain @Autowired
constructor(userRepository: UserRepository, private val runtimeConfigRepository: RuntimeConfigRepository) : OauthBaseDomain(userRepository) {

    private val runtimeConfigConcurrentHashMap = ConcurrentHashMap<String, RuntimeConfig>()

    @PostConstruct
    fun loadRuntimeConfig() {
        synchronized(this) {
            runtimeConfigConcurrentHashMap.clear()
            runtimeConfigRepository.findAll().forEach { runtimeConfig -> runtimeConfigConcurrentHashMap[runtimeConfig.name] = runtimeConfig }
        }
    }

    fun findByName(name: String): RuntimeConfig? = runtimeConfigConcurrentHashMap[name]

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(runtimePo: RuntimePo): RuntimeConfig {
        val runtimeConfigOptional = runtimeConfigRepository.findByName(runtimePo.name!!)
        if (runtimeConfigOptional.isPresent) {
            throw ServerException("参数信息已存在")
        }
        return RuntimeConfig(
                name = runtimePo.name!!,
                value = runtimePo.value,
                configDes = runtimePo.configDes,
                enabled = runtimePo.enabled ?: true,
                covert = true
        ).let {
            runtimeConfigRepository.save(it)
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(runtimePo: RuntimePo): RuntimeConfig =
            runtimeConfigRepository.save(runtimeConfigRepository.getOne(runtimePo.id!!).apply {
                value = runtimePo.value
                enabled = runtimePo.enabled ?: true
                configDes = runtimePo.configDes
            })

    @Transactional
    fun doDelete(idList: MutableList<String>) = runtimeConfigRepository.deleteByIdInAndCovert(idList, true)

    fun doQuery(runtimePo: RuntimePo): Page<RuntimeConfig> =
            runtimeConfigRepository.findAll({ root, _, criteriaBuilder ->
                val predicateList: MutableList<Predicate> = mutableListOf()
                if (!CommonTools.isNullStr(runtimePo.name)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("name").`as`(String::class.java), "%" + runtimePo.name + "%"))
                }
                if (!CommonTools.isNullStr(runtimePo.value)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("value").`as`(String::class.java), "%" + runtimePo.value + "%"))
                }
                if (runtimePo.enabled != null) {
                    predicateList.add(criteriaBuilder.equal(root.get<Any>("enabled"), runtimePo.enabled))
                }
                criteriaBuilder.and(*predicateList.toTypedArray())
            }, buildPageRequest(runtimePo.queryParam!!))

}
