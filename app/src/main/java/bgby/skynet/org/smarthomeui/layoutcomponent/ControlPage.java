package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;
import android.util.Log;

import bgby.skynet.org.uicomponent.cmptpage.PageFragment;

public class ControlPage extends LayoutComponentBaseImpl {

	public static final String TYPE = "page";

	@Override
	public Fragment getFragment() {
		PageFragment fgmt = PageFragment.newInstance(getComponentId());
		Log.d(TYPE, "Create PageFragment");
		return fgmt;
	}


}
