package pers.acp.admin.oauth.constant

/**
 * @author zhang by 10/01/2019
 * @since JDK 11
 */
object OauthApi {
    const val basePath = "/oauth"
    const val currUser = "/userinfo"
    const val modifiableUser = "/moduserlist"
    const val modifiableOrg = "/modorglist"
    const val currMenu = "/menulist"
    const val appConfig = "/app"
    const val updateSecret = "$appConfig/updatesecret"
    const val roleConfig = "/role"
    const val roleList = "$roleConfig/rolelist"
    const val roleCodes = "$roleConfig/rolecodes"
    const val authConfig = "/auth"
    const val menuConfig = "$authConfig/menu"
    const val moduleFuncConfig = "$authConfig/modulefunc"
    const val moduleFuncCodes = "$authConfig/modulefunccodes"
    const val menuList = "$authConfig/menulist"
    const val moduleFuncList = "$authConfig/modulefunclist"
    const val userConfig = "/user"
    const val userResetPwd = "$userConfig/resetpwd"
    const val orgConfig = "/org"
    const val runtimeConfig = "/runtime"
    const val logOut = "/logout"
    const val onlineInfo = "/onlineinfo"
}