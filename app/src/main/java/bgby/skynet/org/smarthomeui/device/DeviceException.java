package bgby.skynet.org.smarthomeui.device;

/**
 * Created by Clariones on 6/28/2016.
 */
public class DeviceException extends Exception {
    public DeviceException() {
    }

    public DeviceException(String detailMessage) {
        super(detailMessage);
    }

    public DeviceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DeviceException(Throwable throwable) {
        super(throwable);
    }
}
