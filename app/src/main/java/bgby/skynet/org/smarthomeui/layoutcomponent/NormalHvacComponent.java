package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import org.skynet.bgby.device.DeviceProfile;

import java.util.Map;

import bgby.skynet.org.uicomponent.normalhvac.INormalHvacDevice;
import bgby.skynet.org.uicomponent.normalhvac.NormalHvacFragment;

public class NormalHvacComponent extends BaseLayoutComponent implements INormalHvacDevice{

public static final String TYPE = "normalHVAC";
	
	public String getType(){
		return TYPE;
	}
	@Override
	protected void initByParameters(Map<String, Object> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCurrentRunningMode() {
		return null;
	}

	@Override
	public Double getCurrentRoomTemperature() {
		return null;
	}

	@Override
	public String getDeviceDisplayName() {
		return (String) getParams().get("deviceID");
	}

	@Override
	public String verifySelfDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames) {
		return null;
	}

	@Override
	public Fragment getFragment() {
		NormalHvacFragment fgmt = NormalHvacFragment.newInstance(this.getComponentRuntimeID());
		NormalHvacFragment fragment = fgmt;
		return fragment;
	}
}
