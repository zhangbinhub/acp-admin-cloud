package pers.acp.admin.log.vo

/**
 * @author zhangbin by 2018-1-17 14:56
 * @since JDK 11
 */
data class ApplicationPo(
        var id: String = "",
        var appName: String = "",
        var secret: String = "",
        var scope: String? = "",
        var identify: String? = "",
        var accessTokenValiditySeconds: Int = 0,
        var refreshTokenValiditySeconds: Int = 0,
        var covert: Boolean = true
)