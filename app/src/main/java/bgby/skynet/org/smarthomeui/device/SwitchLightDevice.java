package bgby.skynet.org.smarthomeui.device;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestandard.SimpleLight;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestRequest;

import java.util.Map;

import bgby.skynet.org.smarthomeui.uicontroller.Helper;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.simplelight.ISimpleLightDevice;

/**
 * Created by Clariones on 6/28/2016.
 */
public class SwitchLightDevice extends DeviceBaseImpl implements ISimpleLightDevice {
    protected boolean state;
    protected boolean canDoToggle;

    public boolean isCanDoToggle() {
        return canDoToggle;
    }

    public void setCanDoToggle(boolean canDoToggle) {
        this.canDoToggle = canDoToggle;
    }

    public SwitchLightDevice() {
        super();
        supportProfile("Honeywell Switch Light");
    }

    @Override
    protected void handleStatusReport(Map<String, String> params) {
        String value = params.get(SimpleLight.TERM_LIGHT_STATUES);
        if (value != null) {
            boolean bVal = DriverUtils.getAsBoolean(value, false);
            this.state = bVal;
        }
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
        exeCmd(SimpleLight.CMD_SET_LIGHT, SimpleLight.TERM_LIGHT_STATUES, state ? SimpleLight.TERM_LIGHT_ON : SimpleLight.TERM_LIGHT_OFF);
    }

    @Override
    protected void onCommandResponse(IRestRequest request, Helper.RestResponseData response, Map<String, Object> result) {
        if (response.getErrorCode() != 0) {
            Controllers.showError("命令执行错误", response.getErrorCode()+":"+response.getResult());
            return;
        }
        Boolean newState = getParamBoolean(result, SimpleLight.TERM_LIGHT_STATUES, null);
        if (newState == null) {
            Controllers.showError("错误的状态值", String.valueOf(result.get(SimpleLight.TERM_LIGHT_STATUES)));
            return;
        }
        state = newState;
        refreshConnectedUIComponents();
        return;
    }

    @Override
    public void initWithProfile(DeviceProfile deviceProfile) throws DeviceException {
        super.initWithProfile(deviceProfile);
        boolean bVal = DriverUtils.getAsBoolean(deviceProfile.getSpec().get(SimpleLight.TERM_CAN_QUERY), false);
        setCanDoQuery(bVal);
        bVal = DriverUtils.getAsBoolean(deviceProfile.getSpec().get(SimpleLight.TERM_CAN_TOGGLE), false);
        setCanDoToggle(bVal);
    }

    @Override
    public void toggleState() {
        if (isCanDoToggle()){
            exeCmd(SimpleLight.CMD_TOGGLE_LIGHT);
        }else {
            setState(!getState());
        }

    }
}
