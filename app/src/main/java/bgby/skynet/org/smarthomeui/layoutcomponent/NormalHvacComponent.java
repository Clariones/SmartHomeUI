package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;

import org.skynet.bgby.deviceprofile.DeviceProfile;

import java.util.Map;

import bgby.skynet.org.uicomponent.normalhvac.INormalHvacDevice;
import bgby.skynet.org.uicomponent.normalhvac.NormalHvacFragment;

public class NormalHvacComponent extends BaseLayoutComponent implements INormalHvacDevice{

public static final String TYPE = "normalHVAC";
	/**
	 * Device specification related keys.
	 */
	public static final String TERM_RUNNING_MODES = "validRunningModes";
	public static final String TERM_HAS_HUMIDITY = "humidityFunction";
	public static final String TERM_ROOM_HUMIDITY = "roomHumidity";
	public static final String TERM_ROOM_TEMPERATURE_RANGE = "roomTemperatureRange";
	public static final String TERM_SET_TEMPERATURE_RANGE = "temperatureSettingRange";
	public static final String TERM_FAN_MODES = "validFanModes";
	public static final String TERM_ROOM_TEMPERATURE = "roomTemperature";
	public static final String TERM_SET_TEMPERATURE = "temperatureSetting";
	public static final String TERM_RUNNING_MODE = "runningMode";
	public static final String TERM_FAN_MODE = "fanMode";

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

	public String getType(){
		return TYPE;
	}
	@Override
	protected void initByParameters(Map<String, Object> params) {
		deviceID = getParamString(params, PARAM_DEVICE_ID, null);
	}

	protected void initWithProfile(){
		Map<String, Object> specParams = getDeviceProfile().getSpec();
		hasHumidity = getParamBoolean(specParams, TERM_HAS_HUMIDITY, false);
		Double[] range = getParamDoubleRange(specParams, TERM_ROOM_TEMPERATURE_RANGE, new Double[]{null, null});
		roomTemperatureLowLimit = range[0];
		roomTemperatureHighLimit = range[1];
		range = getParamDoubleRange(specParams, TERM_SET_TEMPERATURE_RANGE, new Double[]{null, null});
		settingTemperatureLowLimit = range[0];
		settingTemperatureHighLimit = range[1];
		runningModes = getParamStringArray(specParams, TERM_RUNNING_MODES, null);
		fanSpeeds = getParamStringArray(specParams, TERM_FAN_MODES, null);
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

	@Override
	public String verifySelfDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames) {
		if (roomTemperatureLowLimit == null || roomTemperatureHighLimit == null){
			return "类型为" + TYPE +"的设备，必须设定特征参数" + TERM_ROOM_TEMPERATURE_RANGE + "，值为长度为2的Double数组";
		}
		if (settingTemperatureLowLimit == null || settingTemperatureHighLimit ==  null){
			return "类型为" + TYPE +"的设备，必须设定特征参数" + TERM_SET_TEMPERATURE_RANGE + "，值为长度为2的Double数组";
		}
		if (runningModes == null || runningModes.length < 2){
			return "类型为" + TYPE +"的设备，必须设定特征参数" + TERM_RUNNING_MODES + "，值为最小长度为2的字符串数组";
		}
		if (fanSpeeds == null || runningModes.length < 2){
			return "类型为" + TYPE +"的设备，必须设定特征参数" + TERM_FAN_MODES + "，值为最小长度为2的字符串数组";
		}
		return null;
	}

	@Override
	public Fragment getFragment() {
		NormalHvacFragment fgmt = NormalHvacFragment.newInstance(this.getComponentRuntimeID());
		NormalHvacFragment fragment = fgmt;
		return fragment;
	}
}
