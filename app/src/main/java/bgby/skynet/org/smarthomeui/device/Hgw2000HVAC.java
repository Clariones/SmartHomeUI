package bgby.skynet.org.smarthomeui.device;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestandard.NormalHVAC;

import java.util.Map;

import bgby.skynet.org.uicomponent.normalhvac.INormalHvacDevice;

/**
 * Created by Clariones on 6/28/2016.
 */
public class Hgw2000HVAC extends DeviceBaseImpl implements INormalHvacDevice {
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
    public Hgw2000HVAC() {
        super();
        supportProfile("Honeywell HDW 2000 HVAC");
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

        String errMsg = this.verifySelfDeviceConfig(profile.getSpec());
        if (errMsg != null){
            throw new DeviceException(errMsg);
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
    }

    @Override
    public String getDeviceDisplayName() {
        return deviceDisplayName;
    }

    @Override
    public void setDeviceDisplayName(String deviceDisplayName) {
        this.deviceDisplayName = deviceDisplayName;
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
