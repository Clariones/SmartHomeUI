package bgby.skynet.org.smarthomeui.device;

import org.skynet.bgby.deviceprofile.DeviceProfile;

import java.util.Map;
import java.util.Set;

/**
 * Created by Clariones on 6/28/2016.
 */
public interface IDevice {
    String getDeviceId();

    void setDeviceId(String deviceId);

    String getSupportStandard();

    String getProfileId();

    void setProfileId(String profileId);

    String getDisplayName();

    void setDisplayName(String displayName);

    boolean canSupportStandard(String profileName);

    void initWithProfile(DeviceProfile deviceProfile) throws DeviceException;

    void onStatusReportMessage(String fromApp, String fromDevice, Map<String, String> params);

    void refreshConnectedUIComponents();

    boolean isCanDoQuery();

    boolean queryStatus();
}
