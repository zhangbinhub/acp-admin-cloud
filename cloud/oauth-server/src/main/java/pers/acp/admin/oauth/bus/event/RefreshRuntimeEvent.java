package pers.acp.admin.oauth.bus.event;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
public class RefreshRuntimeEvent extends RemoteApplicationEvent {

    private static final long serialVersionUID = 7302203588781324771L;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public RefreshRuntimeEvent() {
    }

    public RefreshRuntimeEvent(Object source, String origin, String destination, String message) {
        super(source, origin, destination);
        this.message = message;
    }

}
