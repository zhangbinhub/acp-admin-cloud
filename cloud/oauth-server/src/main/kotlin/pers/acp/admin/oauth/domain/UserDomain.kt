package pers.acp.admin.oauth.domain

import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.Organization
import pers.acp.admin.oauth.entity.Role
import pers.acp.admin.oauth.entity.User
import pers.acp.admin.oauth.po.UserPo
import pers.acp.admin.oauth.po.UserQueryPo
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

    @Throws(ServerException::class)
    private fun validatePermit(loginNo: String, userPo: UserPo, roleSet: Set<Role>, isCreate: Boolean) {
        val currUserInfo = findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息")
        if (!isSuper(currUserInfo)) {
            if (currUserInfo.levels >= userPo.levels!!) {
                throw ServerException("不能编辑级别比自身大的用户信息")
            }
            currUserInfo.organizationMngSet.forEach {
                if (!userPo.orgIds.contains(it.id)) {
                    throw ServerException("没有权限编辑机构【${it.name}】下的用户，请联系系统管理员")
                }
                if (!userPo.orgMngIds.contains(it.id)) {
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
                if (currUserInfo.levels >= userPo.levels!!) {
                    throw ServerException("不能创建级别比自身大的用户")
                }
            }
        }
    }

    private fun doSave(user: User, userPo: UserPo): User =
            doSaveUser(user.copy(
                    mobile = userPo.mobile!!,
                    name = userPo.name!!,
                    enabled = userPo.enabled!!,
                    levels = userPo.levels!!,
                    sort = userPo.sort,
                    organizationSet = organizationRepository.findAllById(userPo.orgIds).toMutableSet(),
                    organizationMngSet = organizationRepository.findAllById(userPo.orgMngIds).toMutableSet()
            ))

    @Transactional
    fun doSaveUser(user: User): User = userRepository.save(user)

    fun getMobileForOtherUser(mobile: String, userId: String): User? = userRepository.findByMobileAndIdNot(mobile, userId).orElse(null)

    fun findModifiableUserList(loginNo: String): MutableList<User> {
        val user = findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息")
        return if (isSuper(user)) {
            userRepository.findAll()
        } else {
            user.let {
                userRepository.findByLevelsGreaterThan(it.levels)
            }
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(loginNo: String, userPo: UserPo): User {
        val roleSet = roleRepository.findAllById(userPo.roleIds).toMutableSet()
        validatePermit(loginNo, userPo, roleSet, true)
        var checkUser = userRepository.findByLoginNo(userPo.loginNo!!).orElse(null)
        if (checkUser != null) {
            throw ServerException("登录账号已存在，请重新输入")
        }
        checkUser = userRepository.findByMobile(userPo.mobile!!).orElse(null)
        if (checkUser != null) {
            throw ServerException("手机号码已存在，请重新输入")
        }
        return doSave(User(
                loginNo = userPo.loginNo!!,
                password = SHA256Utils.encrypt(SHA256Utils.encrypt(DEFAULT_PASSWORD) + userPo.loginNo!!),
                roleSet = roleSet
        ), userPo)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(loginNo: String, userPo: UserPo): User {
        val roleSet = roleRepository.findAllById(userPo.roleIds).toMutableSet()
        validatePermit(loginNo, userPo, roleSet, false)
        return doSave(userRepository.getOne(userPo.id!!).apply {
            var checkUser = userRepository.findByLoginNoAndIdNot(userPo.loginNo!!, this.id).orElse(null)
            if (checkUser != null) {
                throw ServerException("登录账号已存在，请重新输入")
            }
            checkUser = userRepository.findByMobileAndIdNot(userPo.mobile!!, this.id).orElse(null)
            if (checkUser != null) {
                throw ServerException("手机号码已存在，请重新输入")
            }
            if (this.loginNo != userPo.loginNo) {
                this.loginNo = userPo.loginNo!!
                this.password = SHA256Utils.encrypt(SHA256Utils.encrypt(DEFAULT_PASSWORD) + userPo.loginNo!!)
                removeToken(userPo.loginNo!!)
            }
            this.roleSet = roleSet
        }, userPo)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdatePwd(loginNo: String, userId: String): User =
            userRepository.getOne(userId).apply {
                (findCurrUserInfo(loginNo) ?: throw ServerException("找不到当前用户信息")).let {
                    if (!isSuper(it)) {
                        if (it.levels >= this.levels) {
                            throw ServerException("不能修改级别比自身大或相等的用户信息")
                        }
                    }
                    this.password = SHA256Utils.encrypt(SHA256Utils.encrypt(DEFAULT_PASSWORD) + this.loginNo)
                    userRepository.save(this)
                    removeToken(loginNo)
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
        if (!isSuper(user)) {
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
        applicationRepository.findAllByOrderByIdentifyAscAppNameAsc().forEach { application -> securityTokenService.removeTokensByAppIdAndLoginNo(application.id, loginNo) }
    }

    fun doQuery(userQueryPo: UserQueryPo): Page<UserVo> =
            userRepository.findAll({ root, _, criteriaBuilder ->
                val predicateList = ArrayList<Predicate>()
                if (!CommonTools.isNullStr(userQueryPo.loginNo)) {
                    predicateList.add(criteriaBuilder.equal(root.get<Any>("loginNo").`as`(String::class.java), userQueryPo.loginNo))
                }
                if (!CommonTools.isNullStr(userQueryPo.name)) {
                    predicateList.add(criteriaBuilder.like(root.get<Any>("name").`as`(String::class.java), "%" + userQueryPo.name + "%"))
                }
                if (userQueryPo.enabled != null) {
                    predicateList.add(criteriaBuilder.equal(root.get<Any>("enabled"), userQueryPo.enabled))
                }
                if (!CommonTools.isNullStr(userQueryPo.orgName)) {
                    val joinOrg = root.join<User, Organization>("organizationSet", JoinType.LEFT)
                    predicateList.add(criteriaBuilder.like(joinOrg.get<Any>("name").`as`(String::class.java), "%" + userQueryPo.orgName + "%"))
                }
                if (!CommonTools.isNullStr(userQueryPo.roleName)) {
                    val joinOrg = root.join<User, Role>("roleSet", JoinType.LEFT)
                    predicateList.add(criteriaBuilder.like(joinOrg.get<Any>("name").`as`(String::class.java), "%" + userQueryPo.roleName + "%"))
                }
                criteriaBuilder.and(*predicateList.toTypedArray())
            }, buildPageRequest(userQueryPo.queryParam!!))
                    .map { user ->
                        val userVO = UserVo()
                        BeanUtils.copyProperties(user, userVO)
                        userVO
                    }

    fun getUserInfo(userId: String): User? = userRepository.findById(userId).orElse(null)

    /**
     * 获取指定部门下所有符合角色编码的用户
     */
    private fun getUserListInOrgListByRoleCode(organizations: Collection<Organization>, roleCode: String): MutableList<User> =
            mutableListOf<User>().apply {
                organizations.forEach { org ->
                    getUserListDistinct(org.userSet.filter { user -> user.roleSet.any { role -> role.code == roleCode } })
                }
            }

    /**
     * User集合去重，返回List
     */
    private fun getUserListDistinct(users: Collection<User>): MutableList<User> =
            mutableListOf<User>().apply {
                val userIdList = mutableListOf<String>()
                users.forEach { user ->
                    if (!userIdList.contains(user.id)) {
                        this.add(user)
                        userIdList.add(user.id)
                    }
                }
            }

    fun getUserListByCurrOrgAndRole(loginNo: String, roleCode: String): MutableList<User> =
            findCurrUserInfo(loginNo)?.let { currUser ->
                getUserListInOrgListByRoleCode(currUser.organizationSet, roleCode)
            } ?: throw ServerException("无法获取当前用户信息")

    /**
     * 获取上级部门指定角色的用户
     * @param orgLevel >0上级部门，1上一级，2上二级...；<=0：本部门
     */
    fun getUserListByRelativeOrgAndRole(loginNo: String, orgLevel: Int, roleCode: String): MutableList<User> =
            findCurrUserInfo(loginNo)?.let { currUser ->
                val orgList = mutableListOf<Organization>()
                currUser.organizationSet.forEach { org ->
                    if (orgLevel > 0) {
                        var organization = org
                        for (index in 0 until orgLevel) {
                            val orgOptional = organizationRepository.findById(organization.parentId)
                            if (orgOptional.isPresent) {
                                organization = orgOptional.get()
                                if (index == orgLevel - 1) {
                                    orgList.add(organization)
                                }
                            } else {
                                break
                            }
                        }
                    } else {
                        orgList.add(org)
                    }
                }
                getUserListInOrgListByRoleCode(orgList, roleCode)
            } ?: throw ServerException("无法获取当前用户信息")

    fun getUserListByOrgCodeAndRole(orgCode: String, roleCode: String): MutableList<User> =
            getUserListInOrgListByRoleCode(organizationRepository.findAllByCodeOrderBySortAsc(orgCode), roleCode)

    fun getUserListByRole(roleCode: String): MutableList<User> =
            getUserListDistinct(roleRepository.findAllByCodeOrderBySortAsc(roleCode).flatMap { role -> role.userSet })

    companion object {
        private const val DEFAULT_PASSWORD = "000000"
    }

}
