package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.ISimpleSensorDevice;
import bgby.skynet.org.uicomponent.simplesensor.SimpleSensorFragment;

/**
 * Created by Clariones on 6/14/2016.
 */
public class SimpleSensorComponent extends LayoutComponentBaseImpl {

    public static final String TYPE = "simpleSensor";
    private static final String TAG = "SimpleSensorComponent";

    @Override
    public Fragment getFragment() {
        SimpleSensorFragment fgmt = SimpleSensorFragment.newInstance(getComponentId());
        return fgmt;
    }

    @Override
    protected boolean validDevice(IDevice device) {
        return device instanceof ISimpleSensorDevice;
    }
}
