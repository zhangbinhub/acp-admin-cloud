package pers.acp.admin.config.constant;

/**
 * @author zhang by 10/01/2019
 * @since JDK 11
 */
public interface ConfigApi {

    String basePath = "/config";

    String propertiesConfig = "/properties";

    String propertiesRefresh = propertiesConfig + "/refresh";

}
