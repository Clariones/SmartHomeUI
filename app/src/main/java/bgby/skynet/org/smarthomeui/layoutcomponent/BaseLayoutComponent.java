package bgby.skynet.org.smarthomeui.layoutcomponent;

import org.skynet.bgby.device.DeviceProfile;
import org.skynet.bgby.layout.ILayout;
import org.skynet.bgby.layout.LayoutBaseImpl;
import org.skynet.bgby.layout.LayoutException;

import java.util.Map;

import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.ILayoutComponent;

/**
 * Created by Clariones on 6/2/2016.
 */
public abstract class BaseLayoutComponent extends LayoutBaseImpl implements ILayoutComponent {
    protected String displayName;
    protected String componentID;

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
        return verifySelfDeviceConfig(profiles, deviceProfileNames);
    }
}
