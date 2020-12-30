package pers.acp.admin.common.event

import org.springframework.cloud.bus.event.RemoteApplicationEvent

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
class ReloadDataBusEvent(
    originService: String?,
    destinationService: String?,
    val message: String,
    source: Any = Object()
) : RemoteApplicationEvent(
    source, originService,
    DEFAULT_DESTINATION_FACTORY.getDestination(destinationService)
)
