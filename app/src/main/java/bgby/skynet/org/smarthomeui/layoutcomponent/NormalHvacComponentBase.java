package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import bgby.skynet.org.uicomponent.normalhvac.NormalHvacFragment;

public class NormalHvacComponentBase extends LayoutComponentBaseImpl {

	public static final String TYPE = "normalHVAC";

	@Override
	public Fragment getFragment() {
		NormalHvacFragment fgmt = NormalHvacFragment.newInstance(getComponentId());
		return fgmt;
	}

}