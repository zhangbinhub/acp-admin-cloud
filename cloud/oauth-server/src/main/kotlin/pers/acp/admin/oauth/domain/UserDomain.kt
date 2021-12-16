package pers.acp.admin.oauth.domain

import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.common.vo.OrganizationVo
import pers.acp.admin.common.vo.RoleVo
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
import pers.acp.admin.common.vo.UserVo
import pers.acp.admin.oauth.component.UserPasswordEncrypt
import pers.acp.admin.oauth.constant.OauthConstant
import io.github.zhangbinhub.acp.core.CommonTools
import io.github.zhangbinhub.acp.boot.exceptions.ServerException

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
constructor(
    userRepository: UserRepository,
    private val userPasswordEncrypt: UserPasswordEncrypt,
    private val stringRedisTemplate: StringRedisTemplate,
    private val applicationRepository: ApplicationRepository,
    private val organizationRepository: OrganizationRepository,
    private val roleRepository: RoleRepository,
    private val securityTokenService: SecurityTokenService
) : OauthBaseDomain(userRepository) {

    @Throws(ServerException::class)
    private fun formatUserVo(user: User) = UserVo().apply {
        BeanUtils.copyProperties(user, this)
        this.organizationSet = user.organizationSet.map { org ->
            OrganizationVo().apply { BeanUtils.copyProperties(org, this) }
        }.toMutableSet()
        this.organizationMngSet = user.organizationMngSet.map { org ->
            OrganizationVo().apply { BeanUtils.copyProperties(org, this) }
        }.toMutableSet()
        this.roleSet = user.roleSet.map { role ->
            RoleVo().apply { BeanUtils.copyProperties(role, this) }
        }.toMutableSet()
    }

    @Throws(ServerException::class)
    private fun validatePermit(loginNo: String, userPo: UserPo, roleSet: Set<Role>, isCreate: Boolean) {
        val currUserInfo = getUserInfoByLoginNo(loginNo) ?: throw ServerException("无法获取当前用户信息")
        if (!isSuper(currUserInfo)) {
            if (currUserInfo.levels >= userPo.levels!!) {
                throw ServerException("不能编辑级别比自身大的用户信息")
            }
            getAllOrgList(organizationRepository, currUserInfo.organizationMngSet.toMutableList()).map { it.id }.let {
                userPo.orgIds.forEach { orgId ->
                    if (!it.contains(orgId)) {
                        throw ServerException("没有权限编辑指定机构下的用户，请联系系统管理员")
                    }
                }
                userPo.orgMngIds.forEach { orgId ->
                    if (!it.contains(orgId)) {
                        throw ServerException("没有权限编辑指定机构下的用户，请联系系统管理员")
                    }
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
        doSaveUser(
            user.copy(
                mobile = userPo.mobile!!,
                name = userPo.name!!,
                enabled = userPo.enabled!!,
                levels = userPo.levels!!,
                sort = userPo.sort,
                organizationSet = organizationRepository.findAllById(userPo.orgIds).toMutableSet(),
                organizationMngSet = organizationRepository.findAllById(userPo.orgMngIds).toMutableSet()
            )
        )

    @Transactional
    fun doSaveUser(user: User): User = userRepository.save(user)

    fun getMobileForOtherUser(mobile: String, userId: String): User? =
        userRepository.findByMobileAndIdNot(mobile, userId).orElse(null)

    @Throws(ServerException::class)
    fun findModifiableUserList(loginNo: String): MutableList<UserVo> {
        val user = getUserInfoByLoginNo(loginNo) ?: throw ServerException("无法获取当前用户信息")
        return if (isSuper(user)) {
            userRepository.findAll().map { item -> formatUserVo(item) }
                .toMutableList()
        } else {
            user.let {
                userRepository.findByLevelsGreaterThan(it.levels)
                    .map { item -> formatUserVo(item) }.toMutableList()
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
        return doSave(
            User(
                loginNo = userPo.loginNo!!,
                password = userPasswordEncrypt.encrypt(userPo.loginNo!!, DEFAULT_PASSWORD),
                roleSet = roleSet
            ), userPo
        )
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(loginNo: String, userPo: UserPo): User {
        val roleSet = roleRepository.findAllById(userPo.roleIds).toMutableSet()
        validatePermit(loginNo, userPo, roleSet, false)
        return doSave(userRepository.getById(userPo.id!!).apply {
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
                this.password = userPasswordEncrypt.encrypt(userPo.loginNo!!, DEFAULT_PASSWORD)
                this.lastUpdatePasswordTime = null
                removeToken(userPo.loginNo!!)
            }
            this.roleSet = roleSet
        }, userPo)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdatePwd(loginNo: String, userId: String): User =
        userRepository.getById(userId).apply {
            (getUserInfoByLoginNo(loginNo) ?: throw ServerException("找不到当前用户信息")).let {
                if (!isSuper(it)) {
                    if (it.levels >= this.levels) {
                        throw ServerException("不能修改级别比自身大或相等的用户信息")
                    }
                }
                this.password = userPasswordEncrypt.encrypt(this.loginNo, DEFAULT_PASSWORD)
                this.lastUpdatePasswordTime = null
                userRepository.save(this)
                removeToken(this.loginNo)
            }
        }

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(loginNo: String, idList: MutableList<String>) {
        val user = getUserInfoByLoginNo(loginNo) ?: throw ServerException("找不到当前用户信息")
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

    @Transactional
    @Throws(ServerException::class)
    fun disableUser(loginNo: String) = getUserInfoByLoginNo(loginNo, true)?.apply {
        this.enabled = false
    }?.apply {
        doSaveUser(this)
    } ?: throw ServerException("找不到用户信息【$loginNo】")

    private fun removeToken(loginNo: String) {
        applicationRepository.findAllByOrderByIdentifyAscAppNameAsc()
            .forEach { application -> securityTokenService.removeTokensByAppIdAndLoginNo(application.id, loginNo) }
    }

    fun doQuery(userQueryPo: UserQueryPo): Page<UserVo> =
        userRepository.findAll({ root, query, criteriaBuilder ->
            val predicateList = ArrayList<Predicate>()
            if (!CommonTools.isNullStr(userQueryPo.loginNo)) {
                predicateList.add(
                    criteriaBuilder.equal(
                        root.get<Any>("loginNo").`as`(String::class.java),
                        userQueryPo.loginNo
                    )
                )
            }
            if (!CommonTools.isNullStr(userQueryPo.name)) {
                predicateList.add(
                    criteriaBuilder.like(
                        root.get<Any>("name").`as`(String::class.java),
                        "%" + userQueryPo.name + "%"
                    )
                )
            }
            if (userQueryPo.enabled != null) {
                predicateList.add(criteriaBuilder.equal(root.get<Any>("enabled"), userQueryPo.enabled))
            }
            if (!CommonTools.isNullStr(userQueryPo.orgName)) {
                val subQuery = query.subquery(User::class.java)
                val subRoot = subQuery.from(User::class.java)
                val joinOrg = subRoot.join<User, Organization>("organizationSet", JoinType.LEFT)
                subQuery.select(subRoot.get("id")).where(
                    criteriaBuilder.like(
                        joinOrg.get<Any>("name").`as`(String::class.java),
                        "%" + userQueryPo.orgName + "%"
                    )
                )
                predicateList.add(root.get<Any>("id").`in`(subQuery))
            }
            if (!CommonTools.isNullStr(userQueryPo.roleName)) {
                val subQuery = query.subquery(User::class.java)
                val subRoot = subQuery.from(User::class.java)
                val joinRole = subRoot.join<User, Role>("roleSet", JoinType.LEFT)
                subQuery.select(subRoot.get("id")).where(
                    criteriaBuilder.like(
                        joinRole.get<Any>("name").`as`(String::class.java),
                        "%" + userQueryPo.roleName + "%"
                    )
                )
                predicateList.add(root.get<Any>("id").`in`(subQuery))
            }
            criteriaBuilder.and(*predicateList.toTypedArray())
        }, buildPageRequest(userQueryPo.queryParam!!))
            .map { user -> formatUserVo(user) }

    fun getUserInfoById(userId: String): User? = userRepository.findById(userId).orElse(null)

    @Throws(ServerException::class)
    fun getUserVoById(userId: String): UserVo = userRepository.findById(userId).let {
        if (it.isEmpty) throw ServerException("找不到用户信息")
        formatUserVo(it.get())
    }

    @Throws(ServerException::class)
    fun getUserVoByLoginNo(loginNo: String): UserVo = userRepository.findByLoginNo(loginNo).let {
        if (it.isEmpty) throw ServerException("找不到用户信息")
        formatUserVo(it.get())
    }

    /**
     * 根据ID查询用户信息
     */
    @Throws(ServerException::class)
    fun getUserVoListByIdList(idList: MutableList<String>): MutableList<UserVo> =
        userRepository.findAllById(idList).map { item -> formatUserVo(item) }.toMutableList()

    /**
     * 根据登录号或姓名模糊查询用户
     */
    @Throws(ServerException::class)
    fun getUserVoListByLoginNoOrName(loginNoOrName: String, findAll: Boolean): MutableList<UserVo> =
        userRepository.findByLoginNoLikeOrNameLikeOrderByLoginNoAsc("%$loginNoOrName%", "%$loginNoOrName%")
            .filter { item -> findAll || item.enabled }.map { item -> formatUserVo(item) }.toMutableList()

    /**
     * 获取指定部门下所有符合角色编码的用户
     */
    private fun getUserVoListInOrgListByRoleCode(
        organizations: Collection<Organization>,
        roleCode: List<String>
    ): MutableList<UserVo> =
        mutableListOf<UserVo>().apply {
            organizations.forEach { org ->
                this.addAll(org.userSet.filter { user -> user.roleSet.any { role -> roleCode.contains(role.code) } }
                    .map { item -> formatUserVo(item) }
                    .toMutableList())
            }
        }.let {
            getUserVoListDistinct(it)
        }

    /**
     * User集合去重，返回List
     */
    private fun getUserVoListDistinct(users: Collection<UserVo>): MutableList<UserVo> =
        mutableListOf<UserVo>().apply {
            val userIdList = mutableListOf<String>()
            users.forEach { user ->
                if (!userIdList.contains(user.id)) {
                    this.add(user)
                    userIdList.add(user.id!!)
                }
            }
        }

    @Throws(ServerException::class)
    fun getUserVoListByCurrOrgAndRole(loginNo: String, roleCode: List<String>): MutableList<UserVo> =
        getUserInfoByLoginNo(loginNo)?.let { currUser ->
            getUserVoListInOrgListByRoleCode(currUser.organizationSet, roleCode)
        } ?: throw ServerException("无法获取当前用户信息")

    /**
     * 获取上级部门指定角色的用户
     * @param orgLevelList >0 下级部门，1下一级，2下二级...；=0：本部门；<0 上级部门，-1上一级，-2上二级...
     */
    @Throws(ServerException::class)
    fun getUserVoListByRelativeOrgAndRole(
        loginNo: String,
        orgLevelList: List<Int>,
        roleCode: List<String>
    ): MutableList<UserVo> =
        getUserInfoByLoginNo(loginNo)?.let { currUser ->
            val orgList = mutableListOf<Organization>()
            orgLevelList.forEach { orgLevel ->
                when {
                    orgLevel > 0 -> { // 获取下级
                        val tmpOrg = currUser.organizationSet.toMutableList()
                        for (index in 1..orgLevel) {
                            val children = getRelativeOrgList(index, tmpOrg)
                            if (children.isNotEmpty()) {
                                tmpOrg.clear()
                                tmpOrg.addAll(children)
                            } else {
                                tmpOrg.clear()
                                break
                            }
                        }
                        orgList.addAll(tmpOrg)
                    }
                    orgLevel < 0 -> { // 获取上级
                        val tmpOrg = currUser.organizationSet.toMutableList()
                        for (index in orgLevel until 0) {
                            val parent = getRelativeOrgList(index, tmpOrg)
                            if (parent.isNotEmpty()) {
                                tmpOrg.clear()
                                tmpOrg.addAll(parent)
                            } else {
                                tmpOrg.clear()
                                break
                            }
                        }
                        orgList.addAll(tmpOrg)
                    }
                    else -> { // 本部门
                        orgList.addAll(currUser.organizationSet)
                    }
                }
            }
            getUserVoListInOrgListByRoleCode(orgList, roleCode)
        } ?: throw ServerException("无法获取当前用户信息")

    fun getUserVoListByOrgCodeAndRole(orgCode: List<String>, roleCode: List<String>): MutableList<UserVo> =
        getUserVoListInOrgListByRoleCode(
            organizationRepository.findAllByCodeLikeOrNameLikeOrderBySortAsc(
                "%$orgCode%",
                "%$orgCode%"
            ), roleCode
        )

    fun getUserVoListByRole(roleCode: List<String>): MutableList<UserVo> =
        getUserVoListDistinct(roleRepository.findAllByCodeInOrderBySortAsc(roleCode)
            .flatMap { role -> role.userSet }
            .map { item -> formatUserVo(item) }
            .toMutableList())

    /**
     * 获取相对部门集合
     * @param flag 标识，>0下级，<0上级
     * @param orgList 参考部门集合
     * @return 相对部门集合
     */
    private fun getRelativeOrgList(flag: Int, orgList: Collection<Organization>): Collection<Organization> = when {
        flag > 0 -> { // 获取下级
            organizationRepository.findByParentIdIn(orgList.map { org -> org.id }.toMutableList())
        }
        flag < 0 -> { // 获取上级
            mutableListOf<Organization>().apply {
                orgList.forEach { org ->
                    val parent = organizationRepository.findById(org.parentId)
                    if (parent.isPresent) {
                        this.add(parent.get())
                    }
                }
            }
        }
        else -> {
            orgList
        }
    }

    /**
     * 记录用户密码错误次数
     */
    fun storePasswordErrorTime(username: String): Long =
        "${OauthConstant.passwordErrorTimeKeyPrefix}$username".let { keyString ->
            (stringRedisTemplate.execute { connection ->
                connection.execute("incr", keyString.toByteArray())
            } as Long).also {
                if (it == 1L) {
                    stringRedisTemplate.execute { connection ->
                        connection.execute(
                            "pexpire",
                            keyString.toByteArray(),
                            86400000.toString().toByteArray()
                        )
                    }
                }
            }
        }

    /**
     * 清除用户密码错误次数
     */
    fun clearPasswordErrorTime(username: String) {
        "${OauthConstant.passwordErrorTimeKeyPrefix}$username".let { keyString ->
            stringRedisTemplate.execute { connection ->
                connection.execute("del", keyString.toByteArray())
            }
        }
    }

    companion object {
        private const val DEFAULT_PASSWORD = "000000"
    }

}
