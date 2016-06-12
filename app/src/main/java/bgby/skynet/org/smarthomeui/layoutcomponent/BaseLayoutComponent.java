package bgby.skynet.org.smarthomeui.layoutcomponent;

import org.skynet.bgby.device.DeviceProfile;
import org.skynet.bgby.layout.ILayout;
import org.skynet.bgby.layout.LayoutBaseImpl;
import org.skynet.bgby.layout.LayoutException;

import java.util.List;
import java.util.Map;

import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.ILayoutComponent;

/**
 * Created by Clariones on 6/2/2016.
 */
public abstract class BaseLayoutComponent extends LayoutBaseImpl implements ILayoutComponent {
    protected String displayName;
    protected String componentID;
    protected String deviceID;
    protected DeviceProfile deviceProfile;

    @Override
    public DeviceProfile getDeviceProfile() {
        return deviceProfile;
    }

    @Override
    public void setDeviceProfile(DeviceProfile deviceProfile) {
        this.deviceProfile = deviceProfile;
    }

    public String getComponentID() {
        return componentID;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    @Override
    public String getDeviceID() {
        return deviceID;
    }

    @Override
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getComponentRuntimeID() {
        return componentID;
    }

    @Override
    public void init(ILayout layoutData) throws LayoutException {
        super.init(layoutData);
        componentID = Controllers.regsiterComponent(this);
    }
    
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String verifyDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames) {
        String id = this.getDeviceID();
        if (id != null) {
            setDeviceProfile(profiles.get(deviceProfileNames.get(id)));
            initWithProfile();
        }
        return verifySelfDeviceConfig(profiles, deviceProfileNames);
    }

    protected abstract void initWithProfile();

    protected Boolean getParamBoolean(Map<String, Object> params, String key, Boolean defaultVal) {
        Object value = params.get(key);
        if (value == null){
            return defaultVal;
        }
        if (value instanceof Boolean){
            return (Boolean) value;
        }
        if (value instanceof String){
            String strValue = ((String) value).trim().toLowerCase();
            if (strValue.equals("true") || strValue.equals("yes")){
                return true;
            }else{
                return false;
            }
        }
        return defaultVal;
    }

    protected Double[] getParamDoubleRange(Map<String, Object> params, String key, Double[] defVal) {
        if (defVal.length != 2){
            throw new RuntimeException("getParamDoubleRange() must be given a 2-member Double array as default value");
        }
        Double[] result = new Double[2];
        result[0] = defVal[0];
        result[1] = defVal[1];
        Object value = params.get(key);
        if (!(value instanceof List)){
            return result;
        }

        List values = (List) value;
        for (int i=0;i<2;i++){
            Object val = values.get(i);
            if (val instanceof Double){
                result[i] = (Double) val;
            }else if (val instanceof Integer){
                result[i] = Double.valueOf((Integer) val);
            }else{
                // keep the default value, so nothing to do
            }
        }
        return result;
    }

    protected String[] getParamStringArray(Map<String, Object> params, String key, String[] defVal) {
        Object paramVal = params.get(key);
        if (paramVal == null){
            return defVal;
        }
        if (!(paramVal instanceof List)){
            return new String[]{String.valueOf(paramVal)};
        }
        List<Object> list = (List<Object>) paramVal;
        String[] result = new String[list.size()];
        for(int i=0;i<result.length;i++){
            result[i] = String.valueOf(list.get(i));
        }
        return result;
    }
}
