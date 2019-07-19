package pers.acp.admin.oauth.domain

import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.Organization
import pers.acp.admin.oauth.entity.Role
import pers.acp.admin.oauth.entity.User
import pers.acp.admin.oauth.po.UserPo
import pers.acp.admin.oauth.repo.ApplicationRepository
import pers.acp.admin.oauth.repo.OrganizationRepository
import pers.acp.admin.oauth.repo.RoleRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.admin.oauth.token.SecurityTokenService
import pers.acp.admin.oauth.vo.UserVo
import pers.acp.core.CommonTools
import pers.acp.core.security.SHA256Utils
import pers.acp.spring.boot.exceptions.ServerException

import javax.persistence.criteria.JoinType
import javax.persistence.criteria.Predicate
import java.util.*

/**
 * @author zhang by 19/12/2018
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class UserDomain @Autowired
constructor(userRepository: UserRepository,
            private val applicationRepository: ApplicationRepository,
            private val organizationRepository: OrganizationRepository,
            private val roleRepository: RoleRepository,
            private val securityTokenService: SecurityTokenService) : OauthBaseDomain(userRepository) {

    fun isAdmin(user: OAuth2Authentication): Boolean = isAdmin(findCurrUserInfo(user.name)
            ?: throw ServerException("无法获取当前用户信息"))

    @Throws(ServerException::class)
    private fun validatePermit(loginNo: String, userPO: UserPo, roleSet: Set<Role>, isCreate: Boolean) {
        val currUserInfo = findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息")
        if (!isAdmin(currUserInfo)) {
            if (currUserInfo.levels >= userPO.levels!!) {
                throw ServerException("不能编辑级别比自身大的用户信息")
            }
            currUserInfo.organizationMngSet.forEach {
                if (!userPO.orgIds.contains(it.id)) {
                    throw ServerException("没有权限编辑机构【${it.name}】下的用户，请联系系统管理员")
                }
                if (!userPO.orgMngIds.contains(it.id)) {
                    throw ServerException("没有权限编辑机构【${it.name}】下的用户，请联系系统管理员")
                }
            }
            val roleMinLevel = getRoleMinLevel(currUserInfo)
            roleSet.forEach {
                if (!roleMinLevel.containsKey(it.appId) || roleMinLevel.getValue(it.appId) >= it.levels) {
                    throw ServerException("没有权限编辑角色【${it.name}】，请联系系统管理员")
                }
            }
        } else {
            if (isCreate) {
                if (currUserInfo.levels >= userPO.levels!!) {
                    throw ServerException("不能创建级别比自身大的用户")
                }
            }
        }
    }

    private fun doSave(user: User, userPO: UserPo): User =
            doSaveUser(user.apply {
                mobile = userPO.mobile!!
                name = userPO.name!!
                enabled = userPO.enabled!!
                levels = userPO.levels!!
                sort = userPO.sort
                organizationSet = organizationRepository.findAllById(userPO.orgIds).toMutableSet()
                organizationMngSet = organizationRepository.findAllById(userPO.orgMngIds).toMutableSet()
            })

    @Transactional
    fun doSaveUser(user: User): User = userRepository.save(user)

    fun getMobileForOtherUser(mobile: String, userId: String): User? = userRepository.findByMobileAndIdNot(mobile, userId).orElse(null)

    fun findModifiableUserList(loginNo: String): MutableList<User> {
        val user = findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息")
        return if (isAdmin(user)) {
            userRepository.findAll()
        } else {
            user.let {
                userRepository.findByLevelsGreaterThan(it.levels)
            }
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(loginNo: String, userPO: UserPo): User {
        val roleSet = roleRepository.findAllById(userPO.roleIds).toMutableSet()
        validatePermit(loginNo, userPO, roleSet, true)
        var checkUser = userRepository.findByLoginNo(userPO.loginNo!!).orElse(null)
        if (checkUser != null) {
            throw ServerException("登录账号已存在，请重新输入")
        }
        checkUser = userRepository.findByMobile(userPO.mobile!!).orElse(null)
        if (checkUser != null) {
            throw ServerException("手机号码已存在，请重新输入")
        }
        return doSave(User().apply {
            this.loginNo = userPO.loginNo!!
            this.password = SHA256Utils.encrypt(SHA256Utils.encrypt(DEFAULT_PASSWORD) + userPO.loginNo!!)
            this.roleSet = roleSet
        }, userPO)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(loginNo: String, userPO: UserPo): User {
        val roleSet = roleRepository.findAllById(userPO.roleIds).toMutableSet()
        validatePermit(loginNo, userPO, roleSet, false)
        val userOptional = userRepository.findById(userPO.id!!)
        if (userOptional.isEmpty) {
            throw ServerException("找不到用户信息")
        }
        return doSave(userOptional.get().apply {
            var checkUser = userRepository.findByLoginNoAndIdNot(userPO.loginNo!!, this.id).orElse(null)
            if (checkUser != null) {
                throw ServerException("登录账号已存在，请重新输入")
            }
            checkUser = userRepository.findByMobileAndIdNot(userPO.mobile!!, this.id).orElse(null)
            if (checkUser != null) {
                throw ServerException("手机号码已存在，请重新输入")
            }
            if (this.loginNo != userPO.loginNo) {
                this.loginNo = userPO.loginNo!!
                this.password = SHA256Utils.encrypt(SHA256Utils.encrypt(DEFAULT_PASSWORD) + userPO.loginNo!!)
                removeToken(userPO.loginNo!!)
            }
            this.roleSet = roleSet
        }, userPO)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdatePwd(loginNo: String, userId: String) {
        val userOptional = userRepository.findById(userId)
        if (userOptional.isEmpty) {
            throw ServerException("找不到用户信息")
        }
        userOptional.get().apply {
            (findCurrUserInfo(loginNo) ?: throw ServerException("找不到当前用户信息")).let {
                if (!isAdmin(it)) {
                    if (it.levels >= this.levels) {
                        throw ServerException("不能修改级别比自身大或相等的用户信息")
                    }
                }
                this.password = SHA256Utils.encrypt(SHA256Utils.encrypt(DEFAULT_PASSWORD) + this.loginNo)
                userRepository.save(this)
                removeToken(loginNo)
            }
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(loginNo: String, idList: MutableList<String>) {
        val user = findCurrUserInfo(loginNo) ?: throw ServerException("找不到当前用户信息")
        if (idList.contains(user.id)) {
            throw ServerException("不能删除自己")
        }
        val userList = userRepository.findAllById(idList)
        if (!isAdmin(user)) {
            userList.forEach {
                if (user.levels >= it.levels) {
                    throw ServerException("没有权限做此操作，请联系系统管理员")
                }
            }
        }
        userRepository.deleteByIdIn(idList)
        userList.forEach { item -> removeToken(item.loginNo) }
    }

    private fun removeToken(loginNo: String) {
        applicationRepository.findAllByOrderByAppNameAsc().forEach { application -> securityTokenService.removeTokensByAppIdAndLoginNo(application.id, loginNo) }
    }

    fun doQuery(userPO: UserPo): Page<UserVo> =
            userRepository.findAll({ root, _, criteriaBuilder ->
                val predicateList = ArrayList<Predicate>()
                if (!CommonTools.isNullStr(userPO.loginNo)) {
                    predicateList.add(criteriaBuilder.equal(root.get<Any>("loginNo").`as`(String::class.java), userPO.loginNo))
                }
                if (!CommonTools.isNullStr(userPO.name)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("name").`as`(String::class.java), "%" + userPO.name + "%"))
                }
                if (userPO.enabled != null) {
                    predicateList.add(criteriaBuilder.equal(root.get<Any>("enabled"), userPO.enabled))
                }
                if (!CommonTools.isNullStr(userPO.orgName)) {
                    val joinOrg = root.join<User, Organization>("organizationSet", JoinType.LEFT)
                    predicateList.add(criteriaBuilder.like(joinOrg.get<Any>("name").`as`(String::class.java), "%" + userPO.orgName + "%"))
                }
                if (!CommonTools.isNullStr(userPO.roleName)) {
                    val joinOrg = root.join<User, Role>("roleSet", JoinType.LEFT)
                    predicateList.add(criteriaBuilder.like(joinOrg.get<Any>("name").`as`(String::class.java), "%" + userPO.roleName + "%"))
                }
                criteriaBuilder.and(*predicateList.toTypedArray())
            }, buildPageRequest(userPO.queryParam!!))
                    .map { user ->
                        val userVO = UserVo()
                        BeanUtils.copyProperties(user, userVO)
                        userVO
                    }

    fun getUserInfo(userId: String): User? = userRepository.findById(userId).orElse(null)

    companion object {

        private const val DEFAULT_PASSWORD = "000000"
    }

}
