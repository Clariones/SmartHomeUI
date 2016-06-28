package bgby.skynet.org.uicomponent.normalhvac;

import bgby.skynet.org.smarthomeui.device.IDevice;

/**
 * Created by Clariones on 6/4/2016.
 */
public interface INormalHvacDevice extends IDevice {

    boolean isHasHumidity();

    void setHasHumidity(boolean hasHumidity);

    Double getRoomTemperatureLowLimit();

    void setRoomTemperatureLowLimit(Double roomTemperatureLowLimit);

    Double getRoomTemperatureHighLimit();

    void setRoomTemperatureHighLimit(Double roomTemperatureHighLimit);

    Double getSettingTemperatureLowLimit();

    void setSettingTemperatureLowLimit(Double settingTemperatureLowLimit);

    Double getSettingTemperatureHighLimit();

    void setSettingTemperatureHighLimit(Double settingTemperatureHighLimit);

    String[] getRunningModes();

    void setRunningModes(String[] runningModes);

    String[] getFanModes();

    void setFanSpeeds(String[] fanSpeeds);

    String getRunningMode();

    void setRunningMode(String runningMode);

    Double getRoomTemperature();

    void setRoomTemperature(Double roomTemperature);

    Double getTemperatureSetting();

    void setTemperatureSetting(Double temperatureSetting);

    Double getRoomHumidity();

    void setRoomHumidity(Double roomHumidity);

    String getFanMode();

    void setFanMode(String fanMode);

    String getDeviceDisplayName();

    void setDeviceDisplayName(String deviceDisplayName);
}
