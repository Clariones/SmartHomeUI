package bgby.skynet.org.smarthomeui.layoutcomponent;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.gson.Gson;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestandard.SimpleDimmer;
import org.skynet.bgby.devicestandard.SimpleLight;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.RestRequestImpl;
import org.skynet.bgby.protocol.RestResponseImpl;
import org.skynet.bgby.restserver.IRestClientContext;

import java.util.HashMap;
import java.util.Map;

import bgby.skynet.org.smarthomeui.uicontroller.Helper;
import bgby.skynet.org.smarthomeui.uicontroller.IRestCommandListener;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.simplelight.IResponseListener;
import bgby.skynet.org.uicomponent.simplelight.ISimpleLightDevice;
import bgby.skynet.org.uicomponent.simplelight.SimpleLightFragment;
import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Clariones on 6/14/2016.
 */
public class SimpleLightComponent extends BaseLayoutComponent implements ISimpleLightDevice {
    public static final String TERM_CAN_TOGGLE = "canDoToggle";
    public static final String TERM_CAN_QUERY = "canQueryStatus";

    public static final String TYPE = "simpleLight";
    private static final String TAG = "SimpleLightComponent";
    protected boolean canDoToggle;
    protected boolean canQueryStatus;

    protected boolean state;

    @Override
    protected void initWithProfile() {
        Map<String, Object> specParams = getDeviceProfile().getSpec();
        canDoToggle = getParamBoolean(specParams, TERM_CAN_TOGGLE, false);
        canQueryStatus = getParamBoolean(specParams, TERM_CAN_QUERY, false);
    }

    @Override
    public String verifySelfDeviceConfig(Map<String, DeviceProfile> profiles, Map<String, String> deviceProfileNames) {
        return null;
    }

    @Override
    public Fragment getFragment() {
        SimpleLightFragment fgmt = SimpleLightFragment.newInstance(this.getComponentRuntimeID());
        SimpleLightFragment fragment = fgmt;
        return fragment;
    }

    @Override
    protected void initByParameters(Map<String, Object> params) {
        deviceID = getParamString(params, PARAM_DEVICE_ID, null);
    }

    @Override
    public boolean getState() {
        return state;
    }

    @Override
    public void setState(boolean newState, IResponseListener listener) {
        this.state = newState;
        exeCmdSetLight(state, listener);
    }

    @Override
    public void toggleState(IResponseListener listener) {
        this.state = !state;
        exeCmdSetLight(state, listener);
    }

    private void exeCmdSetLight(boolean isOnState, final IResponseListener listener) {
        IRestRequest request = new RestRequestImpl();
        request.setCommand(SimpleDimmer.CMD_SET_LIGHT);
        Map<String, String> params = new HashMap<>();
        params.put(SimpleLight.TERM_LIGHT_STATUES, isOnState?SimpleLight.TERM_LIGHT_ON : SimpleLight.TERM_LIGHT_OFF);
        request.setParams(params);
        request.setTarget(getDeviceID());
        Controllers.getControllerManager().executeCmd(request, new IRestCommandListener(){
            public void handleResponse(IRestRequest request, IRestClientContext restClientContext, IHttpResponse httpResponse) {
                NanoHTTPD.Response.IStatus statues = httpResponse.getStatus();
                Helper.RestResponseData response;
                Gson gson = new Gson();
                if (statues.equals(NanoHTTPD.Response.Status.OK)) {
                    String jsonStr = httpResponse.getAsString();
                    Log.d(TAG, "Get response " + jsonStr);
                    response = gson.fromJson(jsonStr, Helper.RestResponseData.class);
                    RestResponseImpl restResponse = new RestResponseImpl();
                    if (response.getErrorCode() == 0){
                        Map<String, Object> result = gson.fromJson(response.getData(), Map.class);
                        if (result.containsKey(SimpleLight.TERM_LIGHT_STATUES)){
                            state = DriverUtils.getAsBoolean(result.get(SimpleLight.TERM_LIGHT_STATUES), false);
                        }
                        listener.onResponse(response.getErrorCode(), response.getResult(), result);
                    }else{
                        listener.onResponse(response.getErrorCode(), response.getResult(), null);
                    }
                    return;
                }

                listener.onResponse(statues.getRequestStatus(), statues.getDescription(), null);
            }
        });
    }

}
