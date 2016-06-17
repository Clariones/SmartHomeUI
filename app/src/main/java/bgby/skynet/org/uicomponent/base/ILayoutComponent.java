package bgby.skynet.org.uicomponent.base;

import android.support.v4.app.Fragment;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.layout.ILayout;

import java.util.Map;

/**
 * Created by Clariones on 6/2/2016.
 */
public interface ILayoutComponent extends ILayout {
    public static final String PARAM_DEVICE_ID = "deviceID";

    /**
     *
     * @param profiles
     * @param deviceProfileNames
     * @return null or empty means no error, otherwise it's the error message
     */
    String verifyDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames);

    String verifySelfDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames);

    void setDisplayName(String displayName);

    DeviceProfile getDeviceProfile();

    void setDeviceProfile(DeviceProfile deviceProfile);

    String getDeviceID();

    void setDeviceID(String deviceID);

    String getDisplayName();

    Fragment getFragment();

    String getComponentRuntimeID();
}
