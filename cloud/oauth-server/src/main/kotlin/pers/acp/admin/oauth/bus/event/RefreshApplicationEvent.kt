package pers.acp.admin.oauth.bus.event

import org.springframework.cloud.bus.event.RemoteApplicationEvent

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
class RefreshApplicationEvent : RemoteApplicationEvent {

    var message: String? = null

    constructor()

    constructor(source: Any, origin: String, destination: String?, message: String) : super(source, origin, destination) {
        this.message = message
    }

}
