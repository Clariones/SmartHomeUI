package bgby.skynet.org.uicomponent.simplelight;

import bgby.skynet.org.smarthomeui.device.IDevice;

/**
 * Created by Clariones on 6/16/2016.
 */
public interface ISimpleLightDevice extends IDevice {

    boolean getState();

    void setState(boolean newState);

    void toggleState();
}
