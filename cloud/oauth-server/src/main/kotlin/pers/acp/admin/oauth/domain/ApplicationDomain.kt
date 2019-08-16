package pers.acp.admin.oauth.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.Application
import pers.acp.admin.oauth.po.ApplicationPo
import pers.acp.admin.oauth.repo.ApplicationRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.core.CommonTools
import pers.acp.spring.boot.exceptions.ServerException

import javax.persistence.criteria.Predicate

/**
 * @author zhang by 13/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class ApplicationDomain @Autowired
constructor(userRepository: UserRepository, private val applicationRepository: ApplicationRepository) : OauthBaseDomain(userRepository) {

    fun getAppList(user: OAuth2Authentication): MutableList<Application> {
        val currUserInfo = findCurrUserInfo(user.name) ?: throw ServerException("无法获取当前用户信息")
        return if (isSuper(currUserInfo)) {
            applicationRepository.findAllByOrderByAppNameAsc()
        } else {
            applicationRepository.findById(user.oAuth2Request.clientId).let {
                if (it.isPresent) {
                    mutableListOf(it.get())
                } else {
                    mutableListOf()
                }
            }
        }
    }

    @Transactional
    fun doCreate(applicationPO: ApplicationPo): Application =
            Application().apply {
                appName = applicationPO.appName!!
                secret = CommonTools.getUuid32()
                accessTokenValiditySeconds = applicationPO.accessTokenValiditySeconds
                refreshTokenValiditySeconds = applicationPO.refreshTokenValiditySeconds
                covert = true
            }.let {
                applicationRepository.save(it)
            }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(applicationPO: ApplicationPo): Application {
        val applicationOptional = applicationRepository.findById(applicationPO.id!!)
        if (applicationOptional.isEmpty) {
            throw ServerException("找不到信息")
        }
        return applicationOptional.get().let {
            it.appName = applicationPO.appName!!
            it.accessTokenValiditySeconds = applicationPO.accessTokenValiditySeconds
            it.refreshTokenValiditySeconds = applicationPO.refreshTokenValiditySeconds
            applicationRepository.save(it)
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdateSecret(appId: String): Application {
        val applicationOptional = applicationRepository.findById(appId)
        if (applicationOptional.isEmpty) {
            throw ServerException("找不到信息")
        }
        return applicationOptional.get().let {
            it.secret = CommonTools.getUuid32()
            applicationRepository.save(it)
        }
    }

    @Transactional
    fun doDelete(idList: MutableList<String>) = applicationRepository.deleteByIdInAndCovert(idList, true)

    fun doQuery(applicationPO: ApplicationPo): Page<Application> =
            applicationRepository.findAll({ root, _, criteriaBuilder ->
                val predicateList: MutableList<Predicate> = mutableListOf()
                if (!CommonTools.isNullStr(applicationPO.appName)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("appName").`as`(String::class.java), "%" + applicationPO.appName + "%"))
                }
                criteriaBuilder.and(*predicateList.toTypedArray())
            }, buildPageRequest(applicationPO.queryParam!!))

    fun getApp(appId: String): Application? = applicationRepository.findById(appId).orElse(null)

}
