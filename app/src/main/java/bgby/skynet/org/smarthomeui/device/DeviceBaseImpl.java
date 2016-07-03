package bgby.skynet.org.smarthomeui.device;

import android.util.Log;

import com.google.gson.Gson;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestandard.DeviceStandardBaseImpl;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.RestRequestImpl;
import org.skynet.bgby.protocol.RestResponseImpl;
import org.skynet.bgby.protocol.UdpMessage;
import org.skynet.bgby.restserver.IRestClientContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bgby.skynet.org.smarthomeui.uicontroller.Helper;
import bgby.skynet.org.smarthomeui.uicontroller.IRestCommandListener;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.IUiComponent;
import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Clariones on 6/28/2016.
 */
public abstract class DeviceBaseImpl implements IDevice {
    protected static final int FIELD_DEVICE_STATUES_PREFIX_LEGNTH = 12;
    protected String supportStandard;

    protected String deviceId;
    protected String profileId;
    protected String displayName;
    protected boolean canDoQuery;

    public DeviceBaseImpl() {

    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSupportStandard() {
        return supportStandard;
    }

    @Override
    public String getProfileId() {
        return profileId;
    }

    @Override
    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @Override
    public String getDisplayName() {
        if (displayName == null) {
            return getDeviceId();
        }
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    protected DeviceProfile getProfile() {
        return Controllers.getControllerManager().getDeviceProfile(getDeviceId());
    }

    protected void supportStandard(String standard) {
        if (standard == null || standard.isEmpty()) {
            return;
        }
        String standardName = standard.trim().toLowerCase();
        supportStandard = standardName;
    }

    @Override
    public boolean canSupportStandard(String standards) {
        if (supportStandard == null || supportStandard.isEmpty()) {
            return false;
        }
        String canonicalStdName = standards.trim().toLowerCase();
        return (canonicalStdName.equals(supportStandard));
    }

    protected Boolean getParamBoolean(Map<String, Object> params, String key, Boolean defaultVal) {
        Object value = params.get(key);
        if (value == null) {
            return defaultVal;
        }
        if (defaultVal != null) {
            return DriverUtils.getAsBoolean(value, defaultVal);
        } else {
            return DriverUtils.getAsBoolean(value, false);
        }
    }

    protected Double[] getParamDoubleRange(Map<String, Object> params, String key, Double[] defVal) {
        if (defVal.length != 2) {
            throw new RuntimeException("getParamDoubleRange() must be given a 2-member Double array as default value");
        }
        Double[] result = new Double[2];
        result[0] = defVal[0];
        result[1] = defVal[1];
        Object value = params.get(key);
        if (!(value instanceof List)) {
            return result;
        }

        List values = (List) value;
        for (int i = 0; i < 2; i++) {
            Object val = values.get(i);
            if (val instanceof Double) {
                result[i] = (Double) val;
            } else if (val instanceof Integer) {
                result[i] = Double.valueOf((Integer) val);
            } else {
                // keep the default value, so nothing to do
            }
        }
        return result;
    }

    protected String[] getParamStringArray(Map<String, Object> params, String key, String[] defVal) {
        Object paramVal = params.get(key);
        if (paramVal == null) {
            return defVal;
        }
        if (!(paramVal instanceof List)) {
            return new String[]{String.valueOf(paramVal)};
        }
        List<Object> list = (List<Object>) paramVal;
        String[] result = new String[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = String.valueOf(list.get(i));
        }
        return result;
    }

    @Override
    public void initWithProfile(DeviceProfile deviceProfile) throws DeviceException {
        // by default, profile not needed for simple device
        // Complex device such as HVAC should implement this
    }

    @Override
    public void onStatusReportMessage(String fromApp, String fromDevice, Map<String, String> params) {
        Map<String, String> statusParams = new HashMap<>();
        for (Map.Entry<String, String> ent : params.entrySet()) {
            String msgKeyName = ent.getKey();
            if (msgKeyName.startsWith(UdpMessage.FIELD_DEVICE_STATUES_PREFIX)) {
                statusParams.put(ent.getKey().substring(FIELD_DEVICE_STATUES_PREFIX_LEGNTH), ent.getValue());
            } else {
                statusParams.put(ent.getKey(), ent.getValue());
            }
        }
        handleStatusReport(statusParams);
        refreshConnectedUIComponents();
    }

    @Override
    public void refreshConnectedUIComponents() {
        Set<IUiComponent> uiCmpts = Controllers.getControllerManager().getConnectedUIComponents(this);
        if (uiCmpts == null) {
            return;
        }
        for (IUiComponent cmpt : uiCmpts) {
            cmpt.onDeviceStatusChanged(this);
        }
    }

    @Override
    public boolean queryStatus() {
        if (isCanDoQuery()) {
            exeCmd(DeviceStandardBaseImpl.CMD_GET_ALL);
            return true;
        }
        return false;
    }

    public boolean isCanDoQuery() {
        return canDoQuery;
    }

    public void setCanDoQuery(boolean canDoQuery) {
        this.canDoQuery = canDoQuery;
    }

    protected abstract void handleStatusReport(Map<String, String> params);

    protected void exeCmd(String command, String... params) {
        Map<String, String> reqParams = new HashMap<>();
        if (params != null && params.length > 0) {
            int len = params.length;
            if (len % 2 != 0) {
                throw new RuntimeException("exeCmd() params must be repeated \"key,value\",");
            }
            for (int i = 0; i < len; i += 2) {
                reqParams.put(params[i], params[i + 1]);
            }
        }
        exeCmd(command, reqParams);
    }

    protected void exeCmd(String command, Map<String, String> reqParams) {
        final IRestRequest request = new RestRequestImpl();
        request.setCommand(command);
        request.setParams(reqParams);
        request.setTarget(getDeviceId());
        Controllers.getControllerManager().executeCmd(request, new IRestCommandListener() {
            @Override
            public void handleResponse(IRestRequest request, IRestClientContext restClientContext, IHttpResponse httpResponse) {
                NanoHTTPD.Response.IStatus statues = httpResponse.getStatus();
                Helper.RestResponseData response;
                Gson gson = new Gson();
                if (statues.equals(NanoHTTPD.Response.Status.OK)) {
                    String jsonStr = httpResponse.getAsString();
                    Log.d("TAG", "Get response " + jsonStr);
                    response = gson.fromJson(jsonStr, Helper.RestResponseData.class);
                    RestResponseImpl restResponse = new RestResponseImpl();
                    if (response.getErrorCode() == 0) {
                        Map<String, Object> result = gson.fromJson(response.getData(), Map.class);
                        onCommandResponse(request, response, result);
                    } else {
                        onCommandResponse(request, response, null);
                    }
                    return;
                }
                onCommandErrorResponse(request, httpResponse);
            }
        });
    }

    protected void onCommandErrorResponse(IRestRequest request, IHttpResponse httpResponse) {
        NanoHTTPD.Response.IStatus resStatus = httpResponse.getStatus();
        StringBuilder errSb = new StringBuilder();
        errSb.append(resStatus.getDescription() + " when " + request.getCommand() + " for " + request.getTarget());
        Controllers.showError("命令失败", errSb.toString());
    }

    protected abstract void onCommandResponse(IRestRequest request, Helper.RestResponseData response, Map<String, Object> result);
}
