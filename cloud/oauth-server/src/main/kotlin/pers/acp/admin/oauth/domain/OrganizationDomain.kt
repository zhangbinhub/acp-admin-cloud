package pers.acp.admin.oauth.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pers.acp.admin.oauth.base.OauthBaseDomain
import pers.acp.admin.oauth.entity.Organization
import pers.acp.admin.oauth.entity.User
import pers.acp.admin.oauth.po.OrganizationPo
import pers.acp.admin.oauth.repo.OrganizationRepository
import pers.acp.admin.oauth.repo.UserRepository
import pers.acp.admin.oauth.vo.OrganizationVo
import pers.acp.spring.boot.exceptions.ServerException

/**
 * @author zhang by 16/01/2019
 * @since JDK 11
 */
@Service
@Transactional(readOnly = true)
class OrganizationDomain @Autowired
constructor(userRepository: UserRepository, private val organizationRepository: OrganizationRepository) : OauthBaseDomain(userRepository) {

    fun getOrgList(): MutableList<Organization> =
            organizationRepository.findAll().let {
                val map: MutableMap<String, Organization> = mutableMapOf()
                it.forEach { item ->
                    map[item.id] = item
                }
                sortOrganizationList(formatToTreeList(map))
            }

    private fun sortOrganizationList(organizationList: MutableList<Organization>): MutableList<Organization> =
            organizationList.let { list ->
                list.forEach { organization ->
                    if (organization.children.isNotEmpty()) {
                        sortOrganizationList(organization.children)
                    }
                }
                organizationList.apply {
                    this.sortBy { it.sort }
                }
            }

    /**
     * 获取指定机构集合的所有子机构
     */
    private fun getChildrenOrgList(organizationList: MutableList<Organization>): MutableList<Organization> =
            mutableListOf<Organization>().apply {
                organizationList.map { org -> org.id }.toMutableList().let {
                    this.addAll(organizationRepository.findByParentIdIn(it))
                }
            }

    /**
     * Organization集合去重，返回List
     */
    private fun getUserListDistinct(organizations: MutableList<Organization>): MutableList<Organization> =
            mutableListOf<Organization>().apply {
                val orgIdList = mutableListOf<String>()
                organizations.forEach { organization ->
                    if (!orgIdList.contains(organization.id)) {
                        this.add(organization)
                        orgIdList.add(organization.id)
                    }
                }
            }

    private fun doSave(organization: Organization, organizationPo: OrganizationPo): Organization =
            organizationRepository.save(organization.copy(
                    name = organizationPo.name!!,
                    code = organizationPo.code!!,
                    area = organizationPo.area!!,
                    sort = organizationPo.sort,
                    userSet = userRepository.findAllById(organizationPo.userIds).toMutableSet()
            ).apply {
                parentId = organizationPo.parentId!!
            })

    /**
     * 是否有编辑权限
     *
     * @param user   当前登录用户
     * @param orgIds 机构ID
     * @return true|false
     */
    private fun isNotPermit(user: User, vararg orgIds: String): Boolean =
            !isSuper(user) && !user.organizationMngSet.map { it.id }.toMutableList().containsAll(mutableListOf(*orgIds))

    /**
     * 是否有编辑权限
     *
     * @param loginNo 登录帐号
     * @param orgIds  机构ID
     * @return true|false
     */
    @Throws(ServerException::class)
    private fun isNotPermit(loginNo: String, vararg orgIds: String): Boolean =
            isNotPermit(findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息"), *orgIds)

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(loginNo: String, organizationPo: OrganizationPo): Organization {
        val user = findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息")
        if (isNotPermit(user, organizationPo.parentId!!)) {
            throw ServerException("没有权限做此操作，请联系系统管理员")
        }
        return Organization().let {
            doSave(it, organizationPo).apply {
                user.organizationMngSet.add(this)
                userRepository.save(user)
            }
        }
    }

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(loginNo: String, idList: MutableList<String>) {
        if (isNotPermit(loginNo, *idList.toTypedArray())) {
            throw ServerException("没有权限做此操作，请联系系统管理员")
        }
        organizationRepository.findByParentIdIn(idList).apply {
            if (this.isNotEmpty()) {
                throw ServerException("存在下级机构，不允许删除")
            }
        }
        organizationRepository.deleteByIdIn(idList)
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(loginNo: String, organizationPo: OrganizationPo): Organization {
        val organization = organizationRepository.getOne(organizationPo.id!!)
        if (isNotPermit(loginNo, organization.id)) {
            throw ServerException("没有权限做此操作，请联系系统管理员")
        }
        return doSave(organization, organizationPo)
    }

    @Throws(ServerException::class)
    fun getModOrgList(loginNo: String): MutableList<Organization> =
            (findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息")).organizationMngSet.toMutableList()

    @Throws(ServerException::class)
    fun getCurrAndAllChildrenOrgList(loginNo: String): MutableList<Organization> =
            findCurrUserInfo(loginNo)?.let {
                val orgList = it.organizationSet.toMutableList()
                var children = getChildrenOrgList(orgList)
                while (children.isNotEmpty()) {
                    orgList.addAll(children)
                    children = getChildrenOrgList(children)
                }
                getUserListDistinct(orgList)
            } ?: throw ServerException("无法获取当前用户信息")

    @Throws(ServerException::class)
    fun getOrgInfo(orgId: String): OrganizationVo =
            organizationRepository.getOne(orgId).let { item ->
                OrganizationVo(
                        id = item.id,
                        code = item.code,
                        name = item.name,
                        parentId = item.parentId,
                        sort = item.sort,
                        userIds = item.userSet.map { it.id }.toMutableList()
                )
            }

}
