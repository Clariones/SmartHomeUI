package bgby.skynet.org.smarthomeui.device;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.protocol.UdpMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bgby.skynet.org.uicomponent.base.IUiComponent;

/**
 * Created by Clariones on 6/28/2016.
 */
public class DeviceBaseImpl implements IDevice {
    private static final int FIELD_DEVICE_STATUES_PREFIX_LEGNTH = 12;
    protected Set<String> supportedProfiles;

    protected String deviceId;
    protected String profileId;
    protected String displayName;

    public DeviceBaseImpl() {
        supportedProfiles = new HashSet<>();
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public Set<String> getSupportedProfiles() {
        return supportedProfiles;
    }

    @Override
    public String getProfileId() {
        return profileId;
    }

    @Override
    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    protected void supportProfile(String profile) {
        if (profile == null || profile.isEmpty()) {
            return;
        }
        String profileName = profile.trim().toLowerCase();
        supportedProfiles.add(profileName);
    }

    @Override
    public boolean canProfileBe(String profile) {
        if (supportedProfiles == null || supportedProfiles.isEmpty()) {
            return false;
        }
        String profileName = profile.trim().toLowerCase();
        return supportedProfiles.contains(profileName);
    }

    @Override
    public void onStatusChanged(String fromApp, Map<String, String> params, Set<IUiComponent> uiComponents) {
        Map<String, String> statusParams = new HashMap<>();
        for (Map.Entry<String, String> ent : params.entrySet()) {
            String msgKeyName = ent.getKey();
            if (msgKeyName.startsWith(UdpMessage.FIELD_DEVICE_STATUES_PREFIX)) {
                statusParams.put(ent.getKey().substring(FIELD_DEVICE_STATUES_PREFIX_LEGNTH), ent.getValue());
            } else {
                statusParams.put(ent.getKey(), ent.getValue());
            }
        }
        onDeviceReportMessage(fromApp, statusParams);
        if (uiComponents == null) {
            return;
        }
        Iterator<IUiComponent> it = uiComponents.iterator();
        while (it.hasNext()) {
            IUiComponent uiCmp = it.next();
            uiCmp.onDeviceStatusChanged(this);
        }
    }

    protected void onDeviceReportMessage(String fromApp, Map<String, String> statusParams) {
        // sub class must implements this
        throw new UnsupportedOperationException("Any devic must implement onDeviceReportMessage");
    }

    protected Boolean getParamBoolean(Map<String, Object> params, String key, Boolean defaultVal) {
        Object value = params.get(key);
        if (value == null) {
            return defaultVal;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            String strValue = ((String) value).trim().toLowerCase();
            if (strValue.equals("true") || strValue.equals("yes")) {
                return true;
            } else {
                return false;
            }
        }
        return defaultVal;
    }

    protected Double[] getParamDoubleRange(Map<String, Object> params, String key, Double[] defVal) {
        if (defVal.length != 2) {
            throw new RuntimeException("getParamDoubleRange() must be given a 2-member Double array as default value");
        }
        Double[] result = new Double[2];
        result[0] = defVal[0];
        result[1] = defVal[1];
        Object value = params.get(key);
        if (!(value instanceof List)) {
            return result;
        }

        List values = (List) value;
        for (int i = 0; i < 2; i++) {
            Object val = values.get(i);
            if (val instanceof Double) {
                result[i] = (Double) val;
            } else if (val instanceof Integer) {
                result[i] = Double.valueOf((Integer) val);
            } else {
                // keep the default value, so nothing to do
            }
        }
        return result;
    }

    protected String[] getParamStringArray(Map<String, Object> params, String key, String[] defVal) {
        Object paramVal = params.get(key);
        if (paramVal == null) {
            return defVal;
        }
        if (!(paramVal instanceof List)) {
            return new String[]{String.valueOf(paramVal)};
        }
        List<Object> list = (List<Object>) paramVal;
        String[] result = new String[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = String.valueOf(list.get(i));
        }
        return result;
    }

    @Override
    public void initWithProfile(DeviceProfile deviceProfile) throws DeviceException {
        // by default, profile not needed for simple device
        // Complex device such as HVAC should implement this
    }
}
