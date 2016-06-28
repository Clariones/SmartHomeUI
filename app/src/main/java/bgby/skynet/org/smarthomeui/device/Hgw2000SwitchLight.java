package bgby.skynet.org.smarthomeui.device;

import bgby.skynet.org.uicomponent.simplelight.ISimpleLightDevice;

/**
 * Created by Clariones on 6/28/2016.
 */
public class Hgw2000SwitchLight extends DeviceBaseImpl implements ISimpleLightDevice {
    public Hgw2000SwitchLight() {
        super();
        supportProfile("Honeywell Switch Light");
    }

    protected boolean state;

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public void toggleState() {
        setState(!getState());
    }
}
