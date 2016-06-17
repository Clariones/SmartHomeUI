package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import org.skynet.bgby.deviceprofile.DeviceProfile;

import java.util.Map;

import bgby.skynet.org.smarthomeui.cmptpage.CmptPageFragment;

public class ControlPage extends BaseLayoutGroupComponent {

	public static final String TYPE = "page";
	protected Fragment fragment;
	public String getType(){
		return TYPE;
	}

	@Override
	public String verifySelfDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames) {
		return null; // for page, no parameters are used, so always success when verify
	}

	@Override
	public Fragment getFragment() {
//		if (fragment != null){
//			return fragment;
//		}

		CmptPageFragment fgmt = CmptPageFragment.newInstance(this.getComponentRuntimeID());
		fragment = fgmt;
		return fragment;
	}
}
