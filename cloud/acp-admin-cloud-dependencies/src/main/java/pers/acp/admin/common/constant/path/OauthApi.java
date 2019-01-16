package pers.acp.admin.common.constant.path;

/**
 * @author zhang by 10/01/2019
 * @since JDK 11
 */
public interface OauthApi {

    String basePath = "/oauth";

    String currUser = "/userinfo";

    String currMenu = "/menulist";

    String appConfig = "/app";

    String updateSecret = appConfig + "/updatesecret";

    String roleConfig = "/role";

    String roleCodes = roleConfig + "/rulecodes";

    String authConfig = "/auth";

    String moduleFuncCodes = authConfig + "/modulefunccodes";

    String userConfig = "/user";

    String orgConfig = "/org";

    String runtimeConfig = "/runtime";

}
