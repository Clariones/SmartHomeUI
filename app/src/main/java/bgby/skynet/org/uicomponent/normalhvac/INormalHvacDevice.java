package bgby.skynet.org.uicomponent.normalhvac;

/**
 * Created by Clariones on 6/4/2016.
 */
public interface INormalHvacDevice {
    String getCurrentRunningMode();

    Double getCurrentRoomTemperature();

    String getDeviceDisplayName();
}
