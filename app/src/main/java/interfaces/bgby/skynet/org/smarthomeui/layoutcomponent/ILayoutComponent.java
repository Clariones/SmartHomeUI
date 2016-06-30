package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import java.util.List;
import java.util.Map;

import bgby.skynet.org.smarthomeui.device.IDevice;

/**
 * Created by Clariones on 6/2/2016.
 */
public interface ILayoutComponent {
    public static final String PARAM_DEVICE_ID = "deviceID";

    int getPosition();
    void setPosition(int pos);

    String getDeviceID();

    String getDisplayName();

    Fragment getFragment();

    String getComponentId();

    void setComponentId(String id);

    List<ILayoutComponent> getChildren();

    void setChildren(List<ILayoutComponent> childrenCmpts);

    String verifyParams();

    IDevice getDevice();

    void setDevice(IDevice device);

    void setParams(Map<String, Object> params);

    Map<String, Object> getParams();
}
