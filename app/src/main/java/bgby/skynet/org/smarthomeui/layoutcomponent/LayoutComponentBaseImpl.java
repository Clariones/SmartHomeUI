package bgby.skynet.org.smarthomeui.layoutcomponent;

import org.skynet.bgby.layout.LayoutException;

import java.util.List;
import java.util.Map;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.utils.Controllers;

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
    protected int position;

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition(int pos) {
        this.position = pos;
    }

    @Override
    public String getDeviceID() {
        if (device != null){
            return device.getDeviceId();
        }
        return null;
    }

    @Override
    public String getDisplayName() {
        String disName = null;
        if (device != null){
            disName = device.getDisplayName();
        }
        if (disName == null || disName.isEmpty()){
            disName = Controllers.getControllerManager().getDisplayName(getDeviceID());
        }
        if (disName == null || disName.isEmpty()){
            return displayName;
        }else{
            return disName;
        }
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
    public void setDevice(IDevice device) throws LayoutException{
        if (!validDevice(device)){
            throw new LayoutException(this.getClass().getSimpleName() +" cannot related to a " + device.getSupportStandard() + " device");
        }
        this.device = device;
    }

    protected abstract boolean validDevice(IDevice device);


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
