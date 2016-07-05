package bgby.skynet.org.smarthomeui.device;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.protocol.IRestRequest;

import java.util.Map;

import bgby.skynet.org.smarthomeui.uicontroller.Helper;
import bgby.skynet.org.smarthomeui.utils.Controllers;

/**
 * Created by Clariones on 6/28/2016.
 */
public class NormalHvacDevice extends DeviceBaseImpl implements INormalHvacDevice {
    /**
     * Profile related members
     */
    protected boolean hasHumidity = false;
    protected Double roomTemperatureLowLimit;
    protected Double roomTemperatureHighLimit;
    protected Double settingTemperatureLowLimit;
    protected Double settingTemperatureHighLimit;
    protected String[] runningModes = null;
    protected String[] fanSpeeds = null;
    /**
     * Device status-data related members
     */
    protected String runningMode;
    protected Double roomTemperature;
    protected Double temperatureSetting;
    protected Double roomHumidity;
    protected String fanMode;
    protected String deviceDisplayName;
    public NormalHvacDevice() {
        super();
        supportStandard(NormalHVAC.ID);
    }

    public void initWithProfile(DeviceProfile profile) throws DeviceException {
        Map<String, Object> specParams = profile.getSpec();
        hasHumidity = getParamBoolean(specParams, NormalHVAC.TERM_HAS_HUMIDITY, false);
        Double[] range = getParamDoubleRange(specParams, NormalHVAC.TERM_ROOM_TERPERATURE_RANGE, new Double[]{null, null});
        roomTemperatureLowLimit = range[0];
        roomTemperatureHighLimit = range[1];
        range = getParamDoubleRange(specParams, NormalHVAC.TERM_SET_TEMPERATURE_RANGE, new Double[]{null, null});
        settingTemperatureLowLimit = range[0];
        settingTemperatureHighLimit = range[1];
        runningModes = getParamStringArray(specParams, NormalHVAC.TERM_RUNNING_MODES, null);
        fanSpeeds = getParamStringArray(specParams, NormalHVAC.TERM_FAN_MODES, null);
        canDoQuery = getParamBoolean(specParams, NormalHVAC.TERM_CAN_QUERY, true);
        String errMsg = this.verifySelfDeviceConfig(profile.getSpec());
        if (errMsg != null){
            throw new DeviceException(errMsg);
        }
    }

    protected void updateFromParams(Map<String, ? extends Object> newParams) throws DeviceException{
        Object val = null;
        String key = NormalHVAC.TERM_ROOM_TEMPERATURE;
        val = newParams.get(key);
        if (val != null){
            roomTemperature = asDouble(val);
        }

        key = NormalHVAC.TERM_SET_TEMPERATURE;
        val = newParams.get(key);
        if (val != null){
            temperatureSetting = asDouble(val);
        }

        key = NormalHVAC.TERM_ROOM_HUMIDITY;
        val = newParams.get(key);
        if (val != null){
            roomHumidity = asDouble(val);
        }

        key = NormalHVAC.TERM_RUNNING_MODE;
        val = newParams.get(key);
        if (val != null){
            String newMode = String.valueOf(val);
            if (!isInRange(newMode, runningModes)){
                throw new DeviceException("设备返回无法识别的运行模式" + newMode);
            }
            runningMode = newMode;
        }

        key = NormalHVAC.TERM_FAN_MODE;
        val = newParams.get(key);
        if (val != null){
            String newMode = String.valueOf(val);
            if (!isInRange(newMode, fanSpeeds)){
                throw new DeviceException("设备返回无法识别的风扇模式" + newMode);
            }
            fanMode = newMode;
        }
    }

    @Override
    protected void handleStatusReport(Map<String, String> params) {
        try {
            updateFromParams(params);
        } catch (DeviceException e) {
            e.printStackTrace();
            Controllers.showError("收到无效消息", e.getMessage());
        }
    }

    @Override
    protected void onCommandResponse(IRestRequest request, Helper.RestResponseData response, Map<String, Object> result) {
        if (response.getErrorCode() != 0) {
            errorResponse(response);
            return;
        }
        try {
            updateFromParams(result);
            refreshConnectedUIComponents();
        } catch (DeviceException e) {
            e.printStackTrace();
            Controllers.showError("设备返回无效消息", e.getMessage());
        }
    }

    @Override
    public boolean isHasHumidity() {
        return hasHumidity;
    }

    @Override
    public void setHasHumidity(boolean hasHumidity) {
        this.hasHumidity = hasHumidity;
    }

    @Override
    public Double getRoomTemperatureLowLimit() {
        return roomTemperatureLowLimit;
    }

    @Override
    public void setRoomTemperatureLowLimit(Double roomTemperatureLowLimit) {
        this.roomTemperatureLowLimit = roomTemperatureLowLimit;
    }

    @Override
    public Double getRoomTemperatureHighLimit() {
        return roomTemperatureHighLimit;
    }

    @Override
    public void setRoomTemperatureHighLimit(Double roomTemperatureHighLimit) {
        this.roomTemperatureHighLimit = roomTemperatureHighLimit;
    }

    @Override
    public Double getSettingTemperatureLowLimit() {
        return settingTemperatureLowLimit;
    }

    @Override
    public void setSettingTemperatureLowLimit(Double settingTemperatureLowLimit) {
        this.settingTemperatureLowLimit = settingTemperatureLowLimit;
    }

    @Override
    public Double getSettingTemperatureHighLimit() {
        return settingTemperatureHighLimit;
    }

    @Override
    public void setSettingTemperatureHighLimit(Double settingTemperatureHighLimit) {
        this.settingTemperatureHighLimit = settingTemperatureHighLimit;
    }

    @Override
    public String[] getRunningModes() {
        return runningModes;
    }

    @Override
    public void setRunningModes(String[] runningModes) {
        this.runningModes = runningModes;
    }

    @Override
    public String[] getFanModes() {
        return fanSpeeds;
    }

    @Override
    public void setFanSpeeds(String[] fanSpeeds) {
        this.fanSpeeds = fanSpeeds;
    }

    @Override
    public String getRunningMode() {
        return runningMode;
    }

    @Override
    public void setRunningMode(String runningMode) {
        this.runningMode = runningMode;
        exeCmd(NormalHVAC.CMD_SET_RUNNING_MODE, NormalHVAC.TERM_RUNNING_MODE, String.valueOf(runningMode));
    }

    @Override
    public Double getRoomTemperature() {
        return roomTemperature;
    }

    @Override
    public void setRoomTemperature(Double roomTemperature) {
        this.roomTemperature = roomTemperature;
    }

    @Override
    public Double getTemperatureSetting() {
        return temperatureSetting;
    }

    @Override
    public void setTemperatureSetting(Double temperatureSetting) {
        this.temperatureSetting = temperatureSetting;
        exeCmd(NormalHVAC.CMD_SET_TEMPERATURE, NormalHVAC.TERM_SET_TEMPERATURE, String.valueOf(temperatureSetting));

    }

    @Override
    public Double getRoomHumidity() {
        return roomHumidity;
    }

    @Override
    public void setRoomHumidity(Double roomHumidity) {
        this.roomHumidity = roomHumidity;
    }

    @Override
    public String getFanMode() {
        return fanMode;
    }

    @Override
    public void setFanMode(String fanMode) {
        this.fanMode = fanMode;
        exeCmd(NormalHVAC.CMD_SET_FAN_MODE, NormalHVAC.TERM_FAN_MODE, String.valueOf(fanMode));
    }

    public String verifySelfDeviceConfig(Map<String, Object> params) {
        String TYPE = getProfileId();
        if (roomTemperatureLowLimit == null || roomTemperatureHighLimit == null){
            return "类型为" + TYPE +"的设备，必须设定特征参数" + NormalHVAC.TERM_ROOM_TERPERATURE_RANGE + "，值为长度为2的Double数组";
        }
        if (settingTemperatureLowLimit == null || settingTemperatureHighLimit ==  null){
            return "类型为" + TYPE +"的设备，必须设定特征参数" + NormalHVAC.TERM_SET_TEMPERATURE_RANGE + "，值为长度为2的Double数组";
        }
        if (runningModes == null || runningModes.length < 2){
            return "类型为" + TYPE +"的设备，必须设定特征参数" + NormalHVAC.TERM_RUNNING_MODES + "，值为最小长度为2的字符串数组";
        }
        if (fanSpeeds == null || runningModes.length < 2){
            return "类型为" + TYPE +"的设备，必须设定特征参数" + NormalHVAC.TERM_FAN_MODES + "，值为最小长度为2的字符串数组";
        }
        return null;
    }
}
