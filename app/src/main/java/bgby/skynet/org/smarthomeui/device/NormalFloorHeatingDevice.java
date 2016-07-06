package bgby.skynet.org.smarthomeui.device;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestandard.DeviceStandardBaseImpl;
import org.skynet.bgby.devicestandard.NormalFloorHeating;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestRequest;

import java.util.Map;

import bgby.skynet.org.smarthomeui.uicontroller.Helper;
import bgby.skynet.org.smarthomeui.utils.Controllers;

/**
 * Created by Clariones on 7/5/2016.
 */
public class NormalFloorHeatingDevice extends DeviceBaseImpl implements INormalFloorHeating{
    protected Double roomTemperature;
    protected Double temperatureSetting;
    protected Boolean state;
    protected Double roomTemperatureLowLimit;
    protected Double roomTemperatureHighLimit;
    protected Double temperatureSettingLowLimit;
    protected Double temperatureSettingHighLimit;

    public NormalFloorHeatingDevice() {
        super();
        supportStandard(NormalFloorHeating.ID);
    }

    @Override
    public Double getTemperatureSettingHighLimit() {
        return temperatureSettingHighLimit;
    }

    public void setTemperatureSettingHighLimit(Double temperatureSettingHighLimit) {
        this.temperatureSettingHighLimit = temperatureSettingHighLimit;
    }

    @Override
    public Double getRoomTemperatureLowLimit() {
        return roomTemperatureLowLimit;
    }

    public void setRoomTemperatureLowLimit(Double roomTemperatureLowLimit) {
        this.roomTemperatureLowLimit = roomTemperatureLowLimit;
    }

    @Override
    public Double getRoomTemperatureHighLimit() {
        return roomTemperatureHighLimit;
    }

    public void setRoomTemperatureHighLimit(Double roomTemperatureHighLimit) {
        this.roomTemperatureHighLimit = roomTemperatureHighLimit;
    }

    @Override
    public Double getTemperatureSettingLowLimit() {
        return temperatureSettingLowLimit;
    }

    public void setTemperatureSettingLowLimit(Double temperatureSettingLowLimit) {
        this.temperatureSettingLowLimit = temperatureSettingLowLimit;
    }


    @Override
    public Double getRoomTemperature() {
        return roomTemperature;
    }

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
        exeCmd(NormalFloorHeating.CMD_SET_TEMPERATURE, NormalFloorHeating.TERM_SET_TEMPERATURE, String.valueOf(temperatureSetting));
    }

    @Override
    public Boolean getState() {
        return state;
    }

    @Override
    public void setState(Boolean state) {
        this.state = state;
        exeCmd(NormalFloorHeating.CMD_SET_STATE, NormalFloorHeating.TERM_STATE, state?NormalFloorHeating.TERM_STATE_ON:NormalFloorHeating.TERM_STATE_OFF);
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

    private void updateFromParams(Map<String,? extends Object> newParams) throws DeviceException {
        Object val = null;
        String key = NormalFloorHeating.TERM_ROOM_TEMPERATURE;
        val = newParams.get(key);
        if (val != null){
            roomTemperature = asDouble(val);
        }

        key = NormalFloorHeating.TERM_SET_TEMPERATURE;
        val = newParams.get(key);
        if (val != null){
            temperatureSetting = asDouble(val);
        }

        key = NormalFloorHeating.TERM_STATE;
        val = newParams.get(key);
        if (val != null){
            state = DriverUtils.getAsBoolean(val, false);
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
    public void initWithProfile(DeviceProfile deviceProfile) throws DeviceException {
        Map<String, Object> specParams = deviceProfile.getSpec();

        Double[] range = getParamDoubleRange(specParams, NormalFloorHeating.TERM_ROOM_TERPERATURE_RANGE, new Double[]{null, null});
        roomTemperatureLowLimit = range[0];
        roomTemperatureHighLimit = range[1];
        range = getParamDoubleRange(specParams, NormalFloorHeating.TERM_SET_TEMPERATURE_RANGE, new Double[]{null, null});
        temperatureSettingLowLimit = range[0];
        temperatureSettingHighLimit = range[1];
        canDoQuery = getParamBoolean(specParams, DeviceStandardBaseImpl.TERM_CAN_QUERY, true);
        String errMsg = this.verifySelfDeviceConfig(deviceProfile.getSpec());
        if (errMsg != null){
            throw new DeviceException(errMsg);
        }
    }

    public String verifySelfDeviceConfig(Map<String, Object> params) {
        String TYPE = getProfileId();
        if (roomTemperatureLowLimit == null || roomTemperatureHighLimit == null){
            return "类型为" + TYPE +"的设备，必须设定特征参数" + NormalFloorHeating.TERM_ROOM_TERPERATURE_RANGE + "，值为长度为2的Double数组";
        }
        if (temperatureSettingLowLimit == null || temperatureSettingHighLimit ==  null){
            return "类型为" + TYPE +"的设备，必须设定特征参数" + NormalFloorHeating.TERM_SET_TEMPERATURE_RANGE + "，值为长度为2的Double数组";
        }

        return null;
    }
}
