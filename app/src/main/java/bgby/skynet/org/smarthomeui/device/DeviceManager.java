package bgby.skynet.org.smarthomeui.device;

import android.util.Log;

import org.skynet.bgby.deviceprofile.DeviceProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Clariones on 6/28/2016.
 */
public class DeviceManager {
    private static final String TAG = "DeviceManager";
    protected Map<String, IDevice> allDevices;
    protected List<IDevice> deviceExamples;

    protected void addDeviceExample(IDevice example) throws DeviceException{
        if (deviceExamples == null){
            deviceExamples = new ArrayList<>();
        }
        // CHECK if any profile was duplicated
        Iterator<IDevice> it = deviceExamples.iterator();
        while(it.hasNext()){
            IDevice dvcSample = it.next();
            String pros = dvcSample.getSupportStandard();
            String newStandard = example.getSupportStandard();
            if (pros.equals(newStandard)) {
                throw new DeviceException("Both " + dvcSample.getClass().getSimpleName() + " and " + example.getClass().getSimpleName() + " support standard " + newStandard);
            }
        }
        deviceExamples.add(example);
    }
    public Map<String, IDevice> createFromProfiles(Map<String, DeviceProfile> profiles, Map<String, String> devices) throws DeviceException {
        allDevices = new HashMap<>();
        for(Map.Entry<String, String> ent : devices.entrySet()){
            String devId = ent.getKey();
            String profileId = ent.getValue();
            Log.i(TAG, "Creating device " + devId +" as " + profileId);
            IDevice device = createDevice(devId, profiles.get(profileId));
            allDevices.put(devId, device);
        }
        return allDevices;
    }

    private IDevice createDevice(String devId, DeviceProfile deviceProfile) throws DeviceException {
        String profileId = deviceProfile.getID();
        IDevice example = null;
        for(IDevice dvc : deviceExamples){
            if (dvc.canSupportStandard(deviceProfile.getStandard())){
                example = dvc;
                break;
            }
        }
        if (example == null){
            throw new DeviceException("Cannot create device for " + profileId);
        }
        try {
            IDevice device = example.getClass().newInstance();
            device.setProfileId(profileId);
            device.setDeviceId(devId);
            device.initWithProfile(deviceProfile);
            Log.i(TAG, "Create device " + device.getClass().getSimpleName() + " " + devId);
            return device;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeviceException("Failed to initial " + example.getClass());
        }
    }

    public IDevice getDevice(String id){
        return allDevices.get(id);
    }

    public void initSupportedDevices() throws DeviceException {
        addDeviceExample(new SwitchLightDevice());
        addDeviceExample(new DimmerDevice());
        addDeviceExample(new NormalHvacDevice());
        addDeviceExample(new NormalFloorHeatingDevice());
    }
}
