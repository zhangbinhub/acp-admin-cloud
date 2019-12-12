package pers.acp.admin.common.vo

/**
 * @author zhangbin by 2018-1-17 14:56
 * @since JDK 11
 */
data class ApplicationVo(
        var id: String = "",
        var appName: String = "",
        var secret: String = "",
        var scope: String? = null,
        var identify: String? = null,
        var accessTokenValiditySeconds: Int = 0,
        var refreshTokenValiditySeconds: Int = 0,
        var covert: Boolean = true
)