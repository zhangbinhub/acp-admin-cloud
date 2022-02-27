package pers.acp.admin.log.message

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
data class RouteLogMessage(
    var logId: String = "",
    var remoteIp: String = "",
    var gatewayIp: String? = null,
    var path: String? = null,
    var serverId: String? = null,
    var targetIp: String? = null,
    var targetUri: String? = null,
    var targetPath: String? = null,
    var method: String = "",
    var token: String? = null,
    var requestTime: Long = System.currentTimeMillis(),
    var applyToken: Boolean = false,
    var processTime: Long? = null,
    var responseTime: Long? = null,
    var responseStatus: Int? = null
)
