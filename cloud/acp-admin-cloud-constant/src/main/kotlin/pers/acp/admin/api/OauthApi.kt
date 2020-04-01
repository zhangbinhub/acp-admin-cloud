package pers.acp.admin.api

/**
 * @author zhang by 18/01/2019
 * @since JDK 11
 */
object OauthApi {
    private const val authConfig = "/auth"
    const val basePath = "/oauth"
    const val modifiableUser = "/mod-user-list"
    const val modifiableOrg = "/mod-org-list"
    const val currAndAllChildrenOrg = "/org-list"
    const val currMenu = "/menu-list"
    const val currModuleFunc = "/module-func-list"
    const val appConfig = "/app"
    const val updateSecret = "$appConfig/update-secret"
    const val roleConfig = "/role"
    const val roleList = "$roleConfig/role-list"
    const val roleCodes = "$roleConfig/role-code-list"
    const val menuConfig = "$authConfig/menu"
    const val moduleFuncConfig = "$authConfig/module-func"
    const val moduleFuncCodes = "$authConfig/module-func-code-list"
    const val menuList = "$authConfig/menu-list"
    const val moduleFuncList = "$authConfig/module-func-list"
    const val userConfig = "/user"
    const val userResetPwd = "$userConfig/reset-pwd"
    const val orgConfig = "/org"
    const val runtimeConfig = "/runtime"
    const val logOut = "/logout"
    const val onlineInfo = "/online-info"
    const val currToken = "/token-info"
    const val moduleFunc = "/module-func"
    const val authentication = "/authentication"
    const val currUser = "/user-info"
    const val appInfo = "/application"
    const val currOrgUserList = "/org-user-list"
    const val userList = "/user-list"
    const val runtime = "/runtime"
}