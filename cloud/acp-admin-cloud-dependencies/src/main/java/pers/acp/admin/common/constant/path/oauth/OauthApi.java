package pers.acp.admin.common.constant.path.oauth;

/**
 * @author zhang by 10/01/2019
 * @since JDK 11
 */
public interface OauthApi {

    String basePath = "/oauth";

    String currUser = "/userinfo";

    String modifiableUser = "/moduserlist";

    String modifiableOrg = "/modorglist";

    String currMenu = "/menulist";

    String appConfig = "/app";

    String updateSecret = appConfig + "/updatesecret";

    String roleConfig = "/role";

    String roleList = roleConfig + "/rolelist";

    String roleCodes = roleConfig + "/rolecodes";

    String authConfig = "/auth";

    String menuConfig = authConfig + "/menu";

    String moduleFuncConfig = authConfig + "/modulefunc";

    String moduleFuncCodes = authConfig + "/modulefunccodes";

    String menuList = authConfig + "/menulist";

    String moduleFuncList = authConfig + "/modulefunclist";

    String userConfig = "/user";

    String userResetPwd = userConfig + "/resetpwd";

    String orgConfig = "/org";

    String runtimeConfig = "/runtime";

    String propertiesConfig = "/properties";

    String propertiesRefresh = propertiesConfig + "/refresh";

    String logOut = "/logout";

    String loginInfo = "/logininfo";

    String onlineInfo = "/onlineinfo";

}
