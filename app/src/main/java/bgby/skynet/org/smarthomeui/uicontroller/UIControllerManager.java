package bgby.skynet.org.smarthomeui.uicontroller;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.skynet.bgby.command.management.CmdGetLayout;
import org.skynet.bgby.command.management.CmdGetProfileByDevice;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.layout.ILayout;
import org.skynet.bgby.layout.LayoutData;
import org.skynet.bgby.layout.LayoutUtils;
import org.skynet.bgby.listeningserver.IUdpMessageHandler;
import org.skynet.bgby.listeningserver.ListeningServerException;
import org.skynet.bgby.listeningserver.MessageService;
import org.skynet.bgby.listeningserver.MessageService.UdpMessageHandlingContext;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.RestRequestImpl;
import org.skynet.bgby.protocol.UdpMessage;
import org.skynet.bgby.protocol.UdpMessageCodec;
import org.skynet.bgby.restserver.IRestClientCallback;
import org.skynet.bgby.restserver.IRestClientContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import bgby.skynet.org.smarthomeui.device.DeviceException;
import bgby.skynet.org.smarthomeui.device.Hgw2000HBusLight;
import bgby.skynet.org.smarthomeui.device.Hgw2000HVAC;
import bgby.skynet.org.smarthomeui.device.Hgw2000SwitchLight;
import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.layoutcomponent.ControlPage;
import bgby.skynet.org.smarthomeui.layoutcomponent.NormalHvacComponentBase;
import bgby.skynet.org.smarthomeui.layoutcomponent.SimpleLightComponentBase;
import bgby.skynet.org.smarthomeui.layoutcomponent.SixGridLayoutBase;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.ILayoutComponent;
import bgby.skynet.org.uicomponent.base.IUiComponent;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class UIControllerManager {

    private static final String TAG = "UIControllerManager";

    private IInitialProgressCallback progressCallback;
    private UIControllerConfig startConfig;
    private StringBuilder errorReport;
    private boolean hasError;
    private MessageService messageListeningService;
    private UIControllerRestClient restClient;
    private String queryLayoutResult;
    private CmdGetProfileByDevice.DeviceProfilesRestResult queryProfileResult;
    private List<ILayout> layoutPages;
    private Thread startingThread;
    private boolean starting;
    private LayoutComponentManager layoutComponentManager;
    private DeviceManager deviceManager;

    public LayoutComponentManager getLayoutComponentManager() {
        return layoutComponentManager;
    }

    public void setLayoutComponentManager(LayoutComponentManager layoutComponentManager) {
        this.layoutComponentManager = layoutComponentManager;
    }

    public DeviceManager getDeviceManager() {
        return deviceManager;
    }

    public void setDeviceManager(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    public List<ILayout> getLayoutPages() {
        return layoutPages;
    }


    public void init(UIControllerConfig config) {
        // TODO update the code when new layout component need to support
        this.startConfig = config;
        LayoutUtils.registerLayoutType(ControlPage.TYPE, ControlPage.class);
        LayoutUtils.registerLayoutType(NormalHvacComponentBase.TYPE, NormalHvacComponentBase.class);
        LayoutUtils.registerLayoutType(SixGridLayoutBase.TYPE, SixGridLayoutBase.class);
        LayoutUtils.registerLayoutType(SimpleLightComponentBase.TYPE, SimpleLightComponentBase.class);

        layoutComponentManager = new LayoutComponentManager();
        deviceManager = new DeviceManager();
    }

    private boolean hasError(UIControllerStatus status) {
        if (!starting) {
            return true;
        }
        Helper.setProgress(status, getProgressCallback());
        if (!status.isSuccess()) {
            Helper.reportError(getErrorDetailReport(), getProgressCallback());
            getProgressCallback().onStartingFinished(status.isSuccess());
        }
        debugSleep();
        return !status.isSuccess();
    }

    private void debugSleep() {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//        }
    }

    /**
     *
     */
    public void start() {
        new Throwable("UIController stated").printStackTrace();
        this.startingThread = new Thread() {
            public void run() {
                startStepByStep();
            }
        };
        startingThread.start();
    }

    protected void startStepByStep() {
        this.starting = true;
        clearErrorReport();
        // step1 - 先校验本地的启动参数
        if (hasError(verifyInitialParams())) {
            return;
        }
        // step2 - 根据这些参数，初始化各项服务
        if (hasError(startServices())) {
            return;
        }
        // step3 - 向上位机（驱动代理）请求布局配置
        if (hasError(queryLayout())) {
            return;
        }
        if (hasError(verifyLayout())) {
            return;
        }
        // step4 - 向上位机（驱动代理）请求布局中的设备Profile
        if (hasError(queryDeviceProfiles())) {
            return;
        }
        if (hasError(verifyDeviceProfiles())) {
            return;
        }

        // step5 - 创建布局页面和组件
        if (hasError(createLayoutPages())) {
            return;
        }

        Helper.setProgress(UIControllerStatus.START_COMPLETED, getProgressCallback());
        getProgressCallback().onStartingFinished(true);
    }

    private UIControllerStatus verifyDeviceProfiles() {
        try {
            // first, support which devices
            deviceManager.addDeviceExample(new Hgw2000SwitchLight());
            deviceManager.addDeviceExample(new Hgw2000HBusLight());
            deviceManager.addDeviceExample(new Hgw2000HVAC());

            // second, create their instances according to query result
            Map<String, IDevice> allDevices = deviceManager.createFromProfiles(queryProfileResult.getProfiles(), queryProfileResult.getDevices());

            // next, link layout components and devices
            Iterator<Map.Entry<String, ILayoutComponent>> it = layoutComponentManager.getAllComponents().entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, ILayoutComponent> ent = it.next();
                ILayoutComponent cmpt = ent.getValue();
                Map<String, Object> params = cmpt.getParams();
                if (params == null || !params.containsKey(ILayoutComponent.PARAM_DEVICE_ID)){
                    continue;
                }
                String devId = (String) params.get(ILayoutComponent.PARAM_DEVICE_ID);
                cmpt.setDevice(allDevices.get(devId));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            String errSb = DriverUtils.dumpExceptionToString(e);
            errorReport(errSb.toString());
            return UIControllerStatus.INVALID_DEVICE_DATA;
        }

//        // ----------- below are deprecated
//        StringBuilder errSb = new StringBuilder();
//        for (ILayout page : layoutPages) {
//            if (!(page instanceof ILayoutComponent)) {
//                continue;
//            }
//            ILayoutComponent cmp = (ILayoutComponent) page;
//            String errMsg = cmp.verifyDeviceConfig(queryProfileResult.getProfiles(), queryProfileResult.getDevices());
//            if (errMsg == null || errMsg.isEmpty()) {
//                continue;
//            }
//            errSb.append(errMsg).append("\r\n");
//        }
//        if (errSb.length() > 0) {
//            errorReport(errSb.toString());
//            return UIControllerStatus.INVALID_DEVICE_DATA;
//        }
//
//        createDevices();

        return UIControllerStatus.CREATE_LAYOUT;
    }

    private UIControllerStatus verifyLayout() {
        InputStream ins = null;
        try {
            // first, register which type of layout we can support
            layoutComponentManager.registerLayoutComponentType(ControlPage.TYPE, ControlPage.class);
            layoutComponentManager.registerLayoutComponentType(NormalHvacComponentBase.TYPE, NormalHvacComponentBase.class);
            layoutComponentManager.registerLayoutComponentType(SimpleLightComponentBase.TYPE, SimpleLightComponentBase.class);
            layoutComponentManager.registerLayoutComponentType(SixGridLayoutBase.TYPE, SixGridLayoutBase.class);

            // second, create instances from queried result
            ins = new ByteArrayInputStream(queryLayoutResult.getBytes());
            InputStreamReader reader = new InputStreamReader(ins);
            LayoutData[] datas = new Gson().fromJson(reader, new LayoutData[0].getClass());
            Map<String, ILayoutComponent> layoutComponents = layoutComponentManager.createComponentsFromLayoutData(datas);
            DriverUtils.log(Level.INFO, TAG, "Created " + layoutComponents.size() + " components in " + layoutComponentManager.getRootComponents().size() + " pages");

            // next verify their params
            StringBuilder sb = new StringBuilder();
            for(ILayoutComponent compt : layoutComponentManager.getAllComponents().values()){
                String errMsg = compt.verifyParams();
                if (errMsg != null){
                    sb.append(errMsg).append("\r\n");
                }
            }
            if (sb.length() > 1){
                errorReport(sb.toString());
                return UIControllerStatus.INVALID_LAYOUT;
            }
        } catch (Exception e) {
            String msg = DriverUtils.dumpExceptionToString(e);
            errorReport(msg);
            return UIControllerStatus.INVALID_LAYOUT;
        } finally {
            if (null != ins) {
                try {
                    ins.close();
                } catch (IOException e) {
                }
            }
        }
        return UIControllerStatus.REQUEST_DEVICE_DATA;
    }

    private UIControllerStatus createLayoutPages() {
        // nothing can do here
        return UIControllerStatus.INIT_DEVICES;
    }

    private UIControllerStatus queryDeviceProfiles() {
        IRestRequest request = new RestRequestImpl();
        request.setCommand(CmdGetProfileByDevice.CMD);
        request.setTarget(startConfig.getControllerID());
        InetSocketAddress serverAddress = getDriverProxyAddress();
        IHttpResponse response = null;
        try {
            response = restClient.synchRequest(serverAddress, null, request);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            errorReport(DriverUtils.dumpExceptionToString(e));
            return UIControllerStatus.REQUEST_DEVICE_DATA_FAIL;
        }

        if (!response.getStatus().equals(Status.OK)) {
            errorReport("Error: " + response.getAsString());
            return UIControllerStatus.REQUEST_DEVICE_DATA_FAIL;
        }
        Helper.RestResponseData restResp = new Gson().fromJson(response.getAsString(), Helper.RestResponseData.class);
        if (restResp.getErrorCode() != 0) {
            errorReport("Error: " + restResp.getResult());
            return UIControllerStatus.REQUEST_DEVICE_DATA_FAIL;
        }
        queryProfileResult = Helper.gson.fromJson(restResp.getData(), CmdGetProfileByDevice.DeviceProfilesRestResult.class);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            String profileResultStr = new GsonBuilder().setPrettyPrinting().create().toJson(queryProfileResult);
            Log.d(TAG, "Got device profiles as:\n" + profileResultStr);
        }
        return UIControllerStatus.VERIFY_DEVICE_DATA;
    }

    @NonNull
    private InetSocketAddress getDriverProxyAddress() {
        return new InetSocketAddress(startConfig.getDriverProxyAddress(),
                startConfig.getDriverProxyPort());
    }

    private UIControllerStatus queryLayout() {
        IRestRequest request = new RestRequestImpl();
        request.setCommand(CmdGetLayout.CMD);
        request.setTarget(startConfig.getControllerID());
        InetSocketAddress serverAddress = getDriverProxyAddress();
        IHttpResponse response = null;
        try {
            response = restClient.synchRequest(serverAddress, null, request);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            errorReport(DriverUtils.dumpExceptionToString(e));
            return UIControllerStatus.REQUEST_LAYOUT_FAIL;
        }
        IStatus stat = response.getStatus();
        if (stat.equals(Status.CLIENT_CONNECTION_REFUSED)) {
            errorReport(response.getAsString());
            return UIControllerStatus.NO_DRIVER_PROXY;
        }

        if (!stat.equals(Status.OK)) {
            errorReport(response.getAsString());
            return UIControllerStatus.REQUEST_LAYOUT_FAIL;
        }
//        RestResponseImpl restResp = new Gson().fromJson(response.getAsString(), RestResponseImpl.class);
        Helper.RestResponseData restResp = new Gson().fromJson(response.getAsString(), Helper.RestResponseData.class);
        if (restResp.getErrorCode() != 0) {
            errorReport(restResp.getResult() + "\r\nDetails:\r\n" + restResp.getData());
            return UIControllerStatus.NO_LAYOUT;
        }
        queryLayoutResult = restResp.getData().toString();
        Log.i(TAG, queryLayoutResult);
        return UIControllerStatus.VERIFY_LAYOUT;
    }

    private UIControllerStatus startServices() {
        // 初始化 HttpClient
        this.restClient = new UIControllerRestClient();
        restClient.setConnectionTimeout(30 * 1000);
        restClient.setReadTimeout(30 * 1000);


        // 启动组播监听线程
        this.messageListeningService = new MessageService();
        messageListeningService.setDamon(false);
        messageListeningService.setListeningAddress(this.startConfig.getMulticastAddress().getHostName());
        messageListeningService.setListeningPort(this.startConfig.getMulticastPort());
        messageListeningService.setCodec(new UdpMessageCodec());
        IUdpMessageHandler handler = new MulticastMessageListener();
        messageListeningService.registerHandler(handler);
        try {
            messageListeningService.start();
        } catch (ListeningServerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            String msg = DriverUtils.dumpExceptionToString(e);
            errorReport(msg);
            return UIControllerStatus.START_SERVICES_FAILED;
        }

        return UIControllerStatus.REQUEST_LAYOUT;
    }

    private void clearErrorReport() {
        errorReport = new StringBuilder();
    }

    private String getErrorDetailReport() {
        return errorReport.toString();
    }

    public IInitialProgressCallback getProgressCallback() {
        return progressCallback;
    }

    public void setProgressCallback(IInitialProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    private UIControllerStatus verifyInitialParams() {
        UIControllerConfig config = startConfig;
        this.hasError = false;
        if (startConfig == null) {
            errorReport("启动参数未指定");
            return UIControllerStatus.INVALID_PARAMS;
        }
        if (config.getControllerID() == null) {
            errorReport("控制屏ID未设置");
        }
        if (config.getDriverProxyAddress() == null) {
            errorReport("驱动代理地址未设定");
        }
        if (config.getDriverProxyPort() <= 0) {
            errorReport("驱动代理HTTP端口未设定");
        }
        if (config.getMulticastAddress() == null) {
            errorReport("管理消息监听地址未设定");
        }
        if (config.getMulticastPort() <= 0) {
            errorReport("管理消息监听端口未设定");
        }

        return hasError ? UIControllerStatus.INVALID_PARAMS : UIControllerStatus.STARTED_INITIAL;
    }

    private void errorReport(String string) {
        this.hasError = true;
        errorReport.append(string).append("\r\n");
    }

    public DeviceProfile getDeviceProfile(String deviceId) {
        if (queryProfileResult == null || queryProfileResult.getDevices() == null || queryProfileResult.getProfiles() == null) {
            return null;
        }

        String profileId = queryProfileResult.getDevices().get(deviceId);
        DeviceProfile profile = queryProfileResult.getProfiles().get(profileId);
        return profile;
    }

    public void stopStartingThread() {
        starting = false;
        if (startingThread != null) {
            startingThread.interrupt();
        }
        if (messageListeningService != null) {
            messageListeningService.stop();
        }

    }

    public void executeCmd(final IRestRequest request, final IRestCommandListener listener) {
        InetSocketAddress serverAddress = getDriverProxyAddress();
        restClient.asynchRequest(serverAddress, null, request, new IRestClientCallback() {
            @Override
            public void onRestResponse(IRestClientContext restClientContext, IHttpResponse httpResponse) {
                listener.handleResponse(request, restClientContext, httpResponse);
            }
        });
    }

    private UdpMessage handleUdpMessage(UdpMessage inputMessage) {
        String appId = startConfig.getControllerID();
        String fromAppId = inputMessage.getFromApp();
        if (appId.equals(fromAppId)) {
            Log.d(TAG, "Ignore UDP message from self");
            return null;
        }

        String command = inputMessage.getCommand();
        switch (command) {
            case UdpMessage.CMD_DEVICE_STATUS_REPORT:
                Controllers.handleUdpMsgDeviceReport(inputMessage);
                break;
        }

        return null;
    }

    public void registerDeviceRelatedUIComponent(IDevice device, IUiComponent baseUiComponent) {
        // TODO
    }

    public ILayoutComponent getLayoutComponent(String componetID) {
        if (layoutComponentManager.getAllComponents() == null){
            return null;
        }
        return layoutComponentManager.getAllComponents().get(componetID);
    }

    public void unRegisterDeviceRelatedUIComponent(IDevice device, IUiComponent baseUiComponent) {
        // TODO
    }


    public class MulticastMessageListener implements IUdpMessageHandler {

        @Override
        public void handleMessage(UdpMessageHandlingContext context) {
            context.setServed(true);
            context.setResponseMessage(handleUdpMessage(context.getInputMessage()));
        }

    }


}
