package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.ISimpleDimmerDevice;
import bgby.skynet.org.uicomponent.simpledimmer.SimpleDimmerFragment;

/**
 * Created by Clariones on 6/14/2016.
 */
public class SimpleDimmerComponent extends LayoutComponentBaseImpl {

    public static final String TYPE = "simpleDimmer";
    private static final String TAG = "SimpleDimmerComponent";

    @Override
    public Fragment getFragment() {
        SimpleDimmerFragment fgmt = SimpleDimmerFragment.newInstance(getComponentId());
        return fgmt;
    }

    @Override
    protected boolean validDevice(IDevice device) {
        return device instanceof ISimpleDimmerDevice;
    }
}
