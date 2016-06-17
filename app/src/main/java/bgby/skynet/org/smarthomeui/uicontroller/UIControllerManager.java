package bgby.skynet.org.smarthomeui.uicontroller;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.skynet.bgby.command.management.CmdGetLayout;
import org.skynet.bgby.command.management.CmdGetProfileByDevice;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.layout.ILayout;
import org.skynet.bgby.layout.LayoutUtils;
import org.skynet.bgby.listeningserver.IUdpMessageHandler;
import org.skynet.bgby.listeningserver.ListeningServerException;
import org.skynet.bgby.listeningserver.MessageService;
import org.skynet.bgby.listeningserver.MessageService.UdpMessageHandlingContext;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.RestRequestImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Level;

import bgby.skynet.org.smarthomeui.layoutcomponent.ControlPage;
import bgby.skynet.org.smarthomeui.layoutcomponent.NormalHvacComponent;
import bgby.skynet.org.smarthomeui.layoutcomponent.SimpleLightComponent;
import bgby.skynet.org.smarthomeui.layoutcomponent.SixGridLayout;
import bgby.skynet.org.uicomponent.base.ILayoutComponent;
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

    public List<ILayout> getLayoutPages() {
        return layoutPages;
    }

    public void init(UIControllerConfig config) {
        this.startConfig = config;
        LayoutUtils.registerLayoutType(ControlPage.TYPE, ControlPage.class);
        LayoutUtils.registerLayoutType(NormalHvacComponent.TYPE, NormalHvacComponent.class);
        LayoutUtils.registerLayoutType(SixGridLayout.TYPE, SixGridLayout.class);
        LayoutUtils.registerLayoutType(SimpleLightComponent.TYPE, SimpleLightComponent.class);
    }

    private boolean hasError(UIControllerStatus status) {
        if (!starting){
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
        StringBuilder errSb =  new StringBuilder();
        for(ILayout page: layoutPages){
            if (!(page instanceof ILayoutComponent)){
                continue;
            }
            ILayoutComponent cmp = (ILayoutComponent) page;
            String errMsg = cmp.verifyDeviceConfig(queryProfileResult.getProfiles(), queryProfileResult.getDevices());
            if (errMsg == null || errMsg.isEmpty()){
                continue;
            }
            errSb.append(errMsg).append("\r\n");
        }
        if (errSb.length()>0){
            errorReport(errSb.toString());
            return UIControllerStatus.INVALID_DEVICE_DATA;
        }

        return UIControllerStatus.CREATE_LAYOUT;
    }

    private UIControllerStatus verifyLayout() {
        InputStream ins = null;
        try {
            // first, register which type of layout we can support
            ins = new ByteArrayInputStream(queryLayoutResult.getBytes());
            layoutPages = LayoutUtils.fromJson(ins);
            DriverUtils.log(Level.INFO, TAG, "Created " + layoutPages.size() + " pages");
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
        // TODO Auto-generated method stub
        return UIControllerStatus.INIT_DEVICES;
    }

    private UIControllerStatus queryDeviceProfiles() {
        IRestRequest request = new RestRequestImpl();
        request.setCommand(CmdGetProfileByDevice.CMD);
        request.setTarget(startConfig.getControllerID());
        InetSocketAddress serverAddress = new InetSocketAddress(startConfig.getDriverProxyAddress(),
                startConfig.getDriverProxyPort());
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
//        String data = (String) restResp.getData();
//        System.out.println();
//        System.out.println(data);
        queryProfileResult = Helper.gson.fromJson(restResp.getData(), CmdGetProfileByDevice.DeviceProfilesRestResult.class);
        JsonArray jArray = new JsonArray();
        return UIControllerStatus.VERIFY_DEVICE_DATA;
    }

    private UIControllerStatus queryLayout() {
        IRestRequest request = new RestRequestImpl();
        request.setCommand(CmdGetLayout.CMD);
        request.setTarget(startConfig.getControllerID());
        InetSocketAddress serverAddress = new InetSocketAddress(startConfig.getDriverProxyAddress(),
                startConfig.getDriverProxyPort());
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

    public DeviceProfile getDeviceProfile(String deviceId){
        if (queryProfileResult == null || queryProfileResult.getDevices() == null || queryProfileResult.getProfiles() == null){
            return null;
        }

        String profileId = queryProfileResult.getDevices().get(deviceId);
        DeviceProfile profile = queryProfileResult.getProfiles().get(profileId);
        return profile;
    }
    public void setProgressCallback(IInitialProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

    public void stopStartingThread() {
        starting = false;
        startingThread.interrupt();
    }

    public class MulticastMessageListener implements IUdpMessageHandler {

        @Override
        public void handleMessage(UdpMessageHandlingContext context) {
            // TODO Auto-generated method stub
            System.out.println("handle somthing");
        }

    }
}
