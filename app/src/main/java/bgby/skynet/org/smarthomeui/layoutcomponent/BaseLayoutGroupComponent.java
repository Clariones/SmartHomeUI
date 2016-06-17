package bgby.skynet.org.smarthomeui.layoutcomponent;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.layout.ILayout;
import org.skynet.bgby.layout.LayoutException;
import org.skynet.bgby.layout.LayoutGroupBaseImpl;

import java.util.Map;

import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.ILayoutComponent;

/**
 * Created by Clariones on 6/2/2016.
 */
public abstract class BaseLayoutGroupComponent extends LayoutGroupBaseImpl implements ILayoutComponent {
    protected String displayName = "未命名";
    protected String componentID;
    protected String deviceID;
    protected DeviceProfile deviceProfile;

    public String getComponentID() {
        return componentID;
    }

    public void setComponentID(String componentID) {
        this.componentID = componentID;
    }

    @Override
    public DeviceProfile getDeviceProfile() {
        return deviceProfile;
    }

    @Override
    public void setDeviceProfile(DeviceProfile deviceProfile) {
        this.deviceProfile = deviceProfile;
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
    public String getComponentRuntimeID() {
        return componentID;
    }

    ;

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String verifyDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames) {
        StringBuilder sb = new StringBuilder();
        String errMsg = verifySelfDeviceConfig(profiles, deviceProfileNames);
        if (errMsg != null && !errMsg.isEmpty()) {
            sb.append(errMsg).append("\r\n");
        }
        if (getLayoutContent() != null && !getLayoutContent().isEmpty()){
            for(ILayout layoutCmp : getLayoutContent()){
                if (!(layoutCmp instanceof ILayoutComponent)){
                    continue;
                }
                ILayoutComponent comp = (ILayoutComponent) layoutCmp;

                errMsg = comp.verifyDeviceConfig(profiles, deviceProfileNames);
                if (errMsg != null && !errMsg.isEmpty()) {
                    sb.append(errMsg).append("\r\n");
                }
            }
        }

        if (sb.length() < 1){
            return null;
        }
        return sb.toString();
    }

    @Override
    protected void preInitChildLayoutData(ILayout iLayout, ILayout iLayout1) {
    }

    @Override
    protected void postInitChildLayoutData(ILayout iLayout, ILayout iLayout1) {
    }

    @Override
    protected void initByParameters(Map<String, Object> map) {
    }

    @Override
    public void init(ILayout layoutData) throws LayoutException {
        super.init(layoutData);
        componentID = Controllers.regsiterComponent(this);
    }
}
