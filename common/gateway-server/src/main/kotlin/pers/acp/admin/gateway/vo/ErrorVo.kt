package pers.acp.admin.gateway.vo

/**
 * @author zhang by 27/12/2018 13:07
 * @since JDK 11
 */
data class ErrorVo(
        var code: Int = 0,
        var error: String? = null,
        var errorDescription: String? = null
)