package bgby.skynet.org.uicomponent.base;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.layoutcomponent.ILayoutComponent;

/**
 * Created by Clariones on 6/3/2016.
 */
public interface IUiComponent {
    void setLayoutData(ILayoutComponent layoutData);

    ILayoutComponent getLayoutData();

    String getDisplayName();

    void onDeviceStatusChanged(IDevice device);

    void updateDisplayName(String displayName);
}
