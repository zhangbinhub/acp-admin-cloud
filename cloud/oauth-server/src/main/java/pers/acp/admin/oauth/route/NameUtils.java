package pers.acp.admin.oauth.route;

/**
 * @author zhang by 17/03/2019
 * @since JDK 11
 */
class NameUtils {
    private static final String GENERATED_NAME_PREFIX = "_genkey_";

    static String generateName(int i) {
        return GENERATED_NAME_PREFIX + i;
    }

}
