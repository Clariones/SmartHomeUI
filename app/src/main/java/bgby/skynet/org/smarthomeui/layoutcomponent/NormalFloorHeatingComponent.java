package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.INormalFloorHeating;
import bgby.skynet.org.uicomponent.normalfloorheating.NormalFloorHeatingFragment;

public class NormalFloorHeatingComponent extends LayoutComponentBaseImpl {

	public static final String TYPE = "normalFloorHeating";

	@Override
	public Fragment getFragment() {
		NormalFloorHeatingFragment fgmt = NormalFloorHeatingFragment.newInstance(getComponentId());
		return fgmt;
	}

	@Override
	protected boolean validDevice(IDevice device) {
		return device instanceof INormalFloorHeating;
	}
}