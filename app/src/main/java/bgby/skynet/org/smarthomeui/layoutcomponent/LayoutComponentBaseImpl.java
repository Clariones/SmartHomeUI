package bgby.skynet.org.smarthomeui.layoutcomponent;

import java.util.List;
import java.util.Map;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.uicomponent.base.ILayoutComponent;

/**
 * Created by Clariones on 6/28/2016.
 */
public abstract class LayoutComponentBaseImpl implements ILayoutComponent {
    private static final int FIELD_DEVICE_STATUES_PREFIX_LEGNTH = 12;
    protected Map<String, Object> params;
    protected String componentId;
    protected IDevice device;
    protected List<ILayoutComponent> children;
    protected String displayName;



    @Override
    public String getDeviceID() {
        if (device != null){
            return device.getDeviceId();
        }
        return null;
    }

    @Override
    public String getDisplayName() {
        if (device != null){
            return device.getDisplayName();
        }
        return displayName;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    @Override
    public IDevice getDevice() {
        return device;
    }

    @Override
    public void setDevice(IDevice device) {
        this.device = device;
    }

    @Override
    public List<ILayoutComponent> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<ILayoutComponent> children) {
        this.children = children;
    }

    @Override
    public String verifyParams() {
        return null;
    }


}
