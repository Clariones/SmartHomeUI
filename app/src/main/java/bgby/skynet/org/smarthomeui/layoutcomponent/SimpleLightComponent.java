package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.ISimpleLightDevice;
import bgby.skynet.org.uicomponent.simplelight.SimpleLightFragment;

/**
 * Created by Clariones on 6/14/2016.
 */
public class SimpleLightComponent extends LayoutComponentBaseImpl {

    public static final String TYPE = "simpleLight";
    private static final String TAG = "SimpleLightComponent";

    @Override
    public Fragment getFragment() {
        SimpleLightFragment fgmt = SimpleLightFragment.newInstance(getComponentId());
        return fgmt;
    }

    @Override
    protected boolean validDevice(IDevice device) {
        return device instanceof ISimpleLightDevice;
    }
}
