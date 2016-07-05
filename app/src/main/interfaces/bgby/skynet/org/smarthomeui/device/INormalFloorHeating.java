package bgby.skynet.org.smarthomeui.device;

/**
 * Created by Clariones on 7/5/2016.
 */
public interface INormalFloorHeating extends IDevice{
    Double getTemperatureSettingHighLimit();

    Double getRoomTemperatureLowLimit();

    Double getRoomTemperatureHighLimit();

    Double getTemperatureSettingLowLimit();

    Double getRoomTemperature();

    Double getTemperatureSetting();

    void setTemperatureSetting(Double temperatureSetting);

    Boolean getState();

    void setState(Boolean state);
}
