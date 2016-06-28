package bgby.skynet.org.smarthomeui.device;

import org.skynet.bgby.deviceprofile.DeviceProfile;

import java.util.Map;
import java.util.Set;

import bgby.skynet.org.uicomponent.base.IUiComponent;

/**
 * Created by Clariones on 6/28/2016.
 */
public interface IDevice {
    String getDeviceId();

    void setDeviceId(String deviceId);

    Set<String> getSupportedProfiles();

    String getProfileId();

    void setProfileId(String profileId);

    String getDisplayName();

    void setDisplayName(String displayName);

    boolean canProfileBe(String profileName);

    void onStatusChanged(String fromApp, Map<String, String> params, Set<IUiComponent> uiComponents);

    void initWithProfile(DeviceProfile deviceProfile) throws DeviceException;
}
