package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import org.skynet.bgby.deviceprofile.DeviceProfile;

import java.util.Map;

import bgby.skynet.org.uicomponent.simplelight.ISimpleLightDevice;
import bgby.skynet.org.uicomponent.simplelight.SimpleLightFragment;

/**
 * Created by Clariones on 6/14/2016.
 */
public class SimpleLightComponent extends BaseLayoutComponent implements ISimpleLightDevice {
    public static final String TERM_CAN_TOGGLE = "canDoToggle";
    public static final String TERM_CAN_QUERY = "canQueryStatus";

    public static final String TYPE = "simpleLight";
    protected boolean canDoToggle;
    protected boolean canQueryStatus;

    protected boolean state;

    @Override
    protected void initWithProfile() {
        Map<String, Object> specParams = getDeviceProfile().getSpec();
        canDoToggle = getParamBoolean(specParams, TERM_CAN_TOGGLE, false);
        canQueryStatus = getParamBoolean(specParams, TERM_CAN_QUERY, false);
    }

    @Override
    public String verifySelfDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames) {
        return null;
    }

    @Override
    public Fragment getFragment() {
        SimpleLightFragment fgmt = SimpleLightFragment.newInstance(this.getComponentRuntimeID());
        SimpleLightFragment fragment = fgmt;
        return fragment;
    }

    @Override
    protected void initByParameters(Map<String, Object> params) {
        deviceID = getParamString(params, PARAM_DEVICE_ID, null);
    }

    @Override
    public boolean getState() {
        return state;
    }

    @Override
    public void setState(boolean newState) {
        this.state = newState;
    }

    @Override
    public void toggleState() {
        this.state = !state;
    }
}
