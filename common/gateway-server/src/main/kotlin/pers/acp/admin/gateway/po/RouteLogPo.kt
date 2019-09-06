package pers.acp.admin.gateway.po

/**
 * @author zhang by 21/05/2019
 * @since JDK 11
 */
data class RouteLogPo(
        var remoteIp: String? = null,
        var gatewayIp: String? = null,
        var path: String? = null,
        var serverId: String? = null,
        var targetUri: String? = null,
        var requestTime: Long? = null,
        var processTime: Long? = null,
        var responseTime: Long? = null,
        var responseStatus: Int? = null
)
