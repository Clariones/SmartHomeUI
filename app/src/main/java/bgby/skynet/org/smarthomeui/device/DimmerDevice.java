package bgby.skynet.org.smarthomeui.device;

import org.skynet.bgby.devicestandard.SimpleDimmer;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestRequest;

import java.util.Map;

import bgby.skynet.org.smarthomeui.uicontroller.Helper;
import bgby.skynet.org.smarthomeui.utils.Controllers;

/**
 * Created by Clariones on 6/28/2016.
 */
public class DimmerDevice extends SwitchLightDevice {
    public DimmerDevice() {
        super();
        supportedStands.clear();
        supportStandard(SimpleDimmer.ID);
    }

    protected int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        if (level > 0){
            state = true;
        }else{
            state = false;
        }
    }

    @Override
    public boolean getState() {
        return state;
    }

    @Override
    public void setState(boolean state) {
       this.state = state;
        if (state && level <= 0){
            level = 100;
        }else if (!state && level >= 100){
            level = 0;
        }
        exeCmd(SimpleDimmer.CMD_SET_LIGHT,
                SimpleDimmer.TERM_LIGHT_STATUES, state?SimpleDimmer.TERM_LIGHT_ON:SimpleDimmer.TERM_LIGHT_OFF,
                SimpleDimmer.TERM_DIMMER_LEVEL, String.valueOf(level));
    }

    @Override
    protected void onCommandResponse(IRestRequest request, Helper.RestResponseData response, Map<String, Object> result) {
        if (response.getErrorCode() != 0) {
            Controllers.showError("命令执行错误", response.getErrorCode()+":"+response.getResult());
            return;
        }
        Boolean newState = getParamBoolean(result, SimpleDimmer.TERM_LIGHT_STATUES, null);
        if (newState == null) {
            Controllers.showError("错误的状态值", String.valueOf(result.get(SimpleDimmer.TERM_LIGHT_STATUES)));
            return;
        }
        state = newState;

        Object levelVal = result.get(SimpleDimmer.TERM_DIMMER_LEVEL);
        if (levelVal != null) {
            try {
                int newLevel = DriverUtils.getAsInt(levelVal, newState ? 100 : 0);
            }catch (Exception e){
                Controllers.showError("错误的亮度值", String.valueOf(result.get(SimpleDimmer.TERM_DIMMER_LEVEL)));
                return;
            }
        }
        refreshConnectedUIComponents();
        return;
    }

    @Override
    protected void handleStatusReport(Map<String, String> params) {
        String value = params.get(SimpleDimmer.TERM_LIGHT_STATUES);
        if (value != null) {
            boolean bVal = DriverUtils.getAsBoolean(value, false);
            this.state = bVal;
        }
        value = params.get(SimpleDimmer.TERM_DIMMER_LEVEL);
        if (value != null) {
            int iVal = DriverUtils.getAsInt(value, 0);
            this.level = iVal;
        }
    }

    @Override
    public void toggleState() {
        setState(!getState());
    }
}
