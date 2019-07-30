package pers.acp.admin.oauth.bus.event

import org.springframework.cloud.bus.event.RemoteApplicationEvent

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
class RefreshApplicationEvent(origin: String?, destination: String?, val message: String?, source: Any = Object()) : RemoteApplicationEvent(source, origin, destination)
