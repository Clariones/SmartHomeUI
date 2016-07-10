package bgby.skynet.org.smarthomeui.device;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestandard.SimpleSensor;
import org.skynet.bgby.protocol.IRestRequest;

import java.util.Map;

import bgby.skynet.org.smarthomeui.uicontroller.Helper;

/**
 * Created by Clariones on 6/28/2016.
 */
public class SimpleSensorDevice extends DeviceBaseImpl implements ISimpleSensorDevice {
    protected String defaultUnit;
    protected String measureUnit;
    protected String measureLevel;
    protected Double measureValue;
    protected String measureName;

    public SimpleSensorDevice() {
        super();
        supportStandard(SimpleSensor.ID);
    }

    @Override
    protected void handleStatusReport(Map<String, String> params) {
//        String value = params.get(SimpleLight.TERM_LIGHT_STATUES);
//        if (value != null) {
//            boolean bVal = DriverUtils.getAsBoolean(value, false);
//            this.state = bVal;
//        }
        updateWithParams(params);
    }

    private void updateWithParams(Map<String, ? extends Object> params) {
        Object value =params.get(SimpleSensor.TERM_MEASURE_UNIT);
        if (value != null) {
            measureUnit = String.valueOf(value);
        }else{
            measureUnit = null;
        }
        value = params.get(SimpleSensor.TERM_MEASURE_LEVEL);
        if (value != null){
            measureLevel = String.valueOf(value);
        }
        try {
            measureValue = asDouble(params.get(SimpleSensor.TERM_MEASURE_VALUE));
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCommandResponse(IRestRequest request, Helper.RestResponseData response, Map<String, Object> result) {
        if (response.getErrorCode() != 0) {
            errorResponse(response);
            return;
        }
        updateWithParams(result);
        refreshConnectedUIComponents();
        return;
    }

    @Override
    public void initWithProfile(DeviceProfile deviceProfile) throws DeviceException {
        super.initWithProfile(deviceProfile);
        defaultUnit = String.valueOf(deviceProfile.getSpec().get(SimpleSensor.TERM_MEASURE_UNIT));
        measureName = String.valueOf(deviceProfile.getSpec().get(SimpleSensor.TERM_MEASURE_NAME));
    }

    @Override
    public String getUnit() {
        if (measureUnit != null){
            return measureUnit;
        }
        return defaultUnit;
    }

    @Override
    public Double getMeasureValue() {
        return measureValue;
    }

    @Override
    public String getMeasureLevel() {
        return measureLevel;
    }

    @Override
    public String getMeasureName() {
        return measureName;
    }
}
