package bgby.skynet.org.smarthomeui.device;

/**
 * Created by Clariones on 6/16/2016.
 */
public interface ISimpleLightDevice extends IDevice {

    boolean getState();

    void setState(boolean newState);

    void toggleState();

}
