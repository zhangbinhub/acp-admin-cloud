package pers.acp.admin.oauth.bus.event;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * @author zhang by 19/03/2019
 * @since JDK 11
 */
public class RefreshApplicationEvent extends RemoteApplicationEvent {

    private static final long serialVersionUID = -4068861028403590633L;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public RefreshApplicationEvent() {
    }

    public RefreshApplicationEvent(Object source, String origin, String destination, String message) {
        super(source, origin, destination);
        this.message = message;
    }

}
