package pers.acp.admin.config.constant

/**
 * @author zhang by 10/01/2019
 * @since JDK 11
 */
object ConfigApi {
    const val basePath = "/config"
    const val propertiesConfig = "/properties"
    const val propertiesRefresh = "$propertiesConfig/refresh"
    const val propertiesRefreshApplication = "$propertiesRefresh/application"
    const val propertiesRefreshMatcher = "$propertiesRefresh/matcher"
}