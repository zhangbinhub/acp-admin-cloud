package pers.acp.admin.gateway.message

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
data class RouteLogMessage(
        var logId: String? = null,
        var remoteIp: String? = null,
        var gatewayIp: String? = null,
        var path: String? = null,
        var serverId: String? = null,
        var targetIp: String? = null,
        var targetUri: String? = null,
        var targetPath: String? = null,
        var method: String? = null,
        var token: String? = null,
        var requestTime: Long? = null,
        var applyToken: Boolean = false,
        var processTime: Long? = null,
        var responseTime: Long? = null,
        var responseStatus: Int? = null
)
