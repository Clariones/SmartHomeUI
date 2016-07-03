package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.INormalHvacDevice;
import bgby.skynet.org.uicomponent.normalhvac.NormalHvacFragment;

public class NormalHvacComponent extends LayoutComponentBaseImpl {

	public static final String TYPE = "normalHVAC";

	@Override
	public Fragment getFragment() {
		NormalHvacFragment fgmt = NormalHvacFragment.newInstance(getComponentId());
		return fgmt;
	}

	@Override
	protected boolean validDevice(IDevice device) {
		return device instanceof INormalHvacDevice;
	}
}