package bgby.skynet.org.smarthomeui.device;

/**
 * Created by Clariones on 7/10/2016.
 */
public interface ISimpleSensorDevice extends IDevice{
    String getUnit();

    Double getMeasureValue();

    String getMeasureLevel();

    String getMeasureName();
}
