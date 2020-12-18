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
constructor(userRepository: UserRepository, private val organizationRepository: OrganizationRepository) :
    OauthBaseDomain(userRepository) {

    fun getAllOrgList(): MutableList<Organization> = organizationRepository.findAllByOrderBySortAsc()

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

    @Throws(ServerException::class)
    private fun doSave(userInfo: User, organization: Organization, organizationPo: OrganizationPo): Organization =
        userRepository.findAllById(organizationPo.userIds).toMutableSet().let { userSetPo ->
            if (validateModifyUserSet(userInfo, organization.userSet, userSetPo)) {
                organizationRepository.save(organization.copy(
                    name = organizationPo.name!!,
                    code = organizationPo.code!!,
                    area = organizationPo.area!!,
                    sort = organizationPo.sort,
                    userSet = userSetPo
                ).apply {
                    parentId = organizationPo.parentId!!
                })
            } else {
                throw ServerException("不合法的操作，不允许修改更高级别的用户列表！")
            }
        }

    /**
     * 是否有编辑权限
     *
     * @param user   当前登录用户
     * @param orgIds 机构ID
     * @return true|false
     */
    private fun isNotPermit(user: User, vararg orgIds: String): Boolean =
        !isSuper(user) && !getAllOrgList(organizationRepository, user.organizationMngSet.toMutableList())
            .map { it.id }.toMutableList().containsAll(mutableListOf(*orgIds))

    @Transactional
    @Throws(ServerException::class)
    fun doCreate(loginNo: String, organizationPo: OrganizationPo): Organization =
        findCurrUserInfo(loginNo)?.let { userInfo ->
            if (isNotPermit(userInfo, organizationPo.parentId!!)) {
                throw ServerException("没有权限做此操作，请联系系统管理员")
            }
            return Organization().let {
                doSave(userInfo, it, organizationPo).apply {
                    userInfo.organizationMngSet.add(this)
                    userRepository.save(userInfo)
                }
            }
        } ?: throw ServerException("无法获取当前用户信息")

    @Transactional
    @Throws(ServerException::class)
    fun doDelete(loginNo: String, idList: MutableList<String>) {
        findCurrUserInfo(loginNo)?.let { userInfo ->
            if (isNotPermit(userInfo, *idList.toTypedArray())) {
                throw ServerException("没有权限做此操作，请联系系统管理员")
            }
            organizationRepository.findByParentIdIn(idList).apply {
                if (this.isNotEmpty()) {
                    throw ServerException("存在下级机构，不允许删除")
                }
            }
            organizationRepository.deleteByIdIn(idList)
        } ?: throw ServerException("无法获取当前用户信息")
    }

    @Transactional
    @Throws(ServerException::class)
    fun doUpdate(loginNo: String, organizationPo: OrganizationPo): Organization =
        findCurrUserInfo(loginNo)?.let { userInfo ->
            val organization = organizationRepository.getOne(organizationPo.id!!)
            if (isNotPermit(userInfo, organization.id)) {
                throw ServerException("没有权限做此操作，请联系系统管理员")
            }
            doSave(userInfo, organization, organizationPo)
        } ?: throw ServerException("无法获取当前用户信息")

    @Throws(ServerException::class)
    fun getModOrgList(loginNo: String): MutableList<Organization> =
        (findCurrUserInfo(loginNo) ?: throw ServerException("无法获取当前用户信息")).organizationMngSet.toMutableList()

    @Throws(ServerException::class)
    fun getCurrAndAllChildrenForOrg(loginNo: String): MutableList<Organization> =
        findCurrUserInfo(loginNo)?.let {
            getAllOrgList(organizationRepository, it.organizationSet.toMutableList())
        } ?: throw ServerException("无法获取当前用户信息")

    @Throws(ServerException::class)
    fun getCurrAndAllChildrenForMngOrg(loginNo: String): MutableList<Organization> =
        findCurrUserInfo(loginNo)?.let {
            getAllOrgList(organizationRepository, it.organizationMngSet.toMutableList())
        } ?: throw ServerException("无法获取当前用户信息")

    @Throws(ServerException::class)
    fun getCurrAndAllChildrenForAllOrg(loginNo: String): MutableList<Organization> =
        findCurrUserInfo(loginNo)?.let { user ->
            mutableListOf<Organization>().let { list ->
                list.addAll(user.organizationSet.toMutableList())
                list.addAll(user.organizationMngSet.toMutableList())
                getAllOrgList(organizationRepository, list)
            }
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

    @Throws(ServerException::class)
    fun getOrgInfoByCodeOrName(codeOrName: String): MutableList<OrganizationVo> =
        mutableListOf<OrganizationVo>().apply {
            organizationRepository.findAllByCodeLikeOrNameLikeOrderBySortAsc("%$codeOrName%", "%$codeOrName%")
                .forEach { item ->
                    this.add(
                        OrganizationVo(
                            id = item.id,
                            code = item.code,
                            name = item.name,
                            parentId = item.parentId,
                            sort = item.sort
                        )
                    )
                }
        }
}
