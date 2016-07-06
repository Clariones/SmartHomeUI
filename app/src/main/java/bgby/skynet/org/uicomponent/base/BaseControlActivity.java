package bgby.skynet.org.uicomponent.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.layoutcomponent.ILayoutComponent;
import bgby.skynet.org.smarthomeui.uicontroller.UIControllerManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;

/**
 * Created by Clariones on 7/6/2016.
 */
public abstract class BaseControlActivity extends Activity implements IUiComponent{
    protected static final String TAG = "BaseControlActivity";
    protected ILayoutComponent layoutData;
    protected String curScreenDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // step1: normal initial works for View
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        Controllers.setScreenDirection(this);

        // step2: bind activity to correct layoutComponent
        Bundle bundle = this.getIntent().getExtras();
        String layoutId = bundle.getString(BaseUiComponent.ARG_COMPONENT_ID);
        UIControllerManager uiCtrl = Controllers.getControllerManager();
        ILayoutComponent layout = uiCtrl.getLayoutComponent(layoutId);
        setLayoutData(layout);

        String deviceId = layout.getDeviceID();
        Log.i(TAG, "Create with device ID " + deviceId);
        Controllers.getControllerManager().registerDeviceRelatedUIComponent(layout.getDevice(), this);
    }

    protected String formatTemperature(float progress) {
        return String.format(Locale.ENGLISH, "%3.1f", progress);
    }

    protected String getScreenDirection() {
        int direction = getRequestedOrientation();
        if (direction == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT || direction == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return "portrait";
        } else {
            return "landscape";
        }
    }

    public ILayoutComponent getLayoutData() {
        return layoutData;
    }

    public void setLayoutData(ILayoutComponent layoutData) {
        this.layoutData = layoutData;
    }

    @Override
    protected void onDestroy() {
        Controllers.getControllerManager().unRegisterDeviceRelatedUIComponent(layoutData.getDevice(), this);
        super.onDestroy();
    }

    @Override
    public String getDisplayName() {
        IDevice device = getLayoutData().getDevice();
        if (device == null) {
            return null;
        }
        return device.getDisplayName();
    }

    protected String getMaterialID(String materialName){
        return getMaterialPrefix() + getCurScreenDirection() + materialName;
    }
    protected abstract String getMaterialPrefix();

    public String getCurScreenDirection() {
        return curScreenDirection;
    }

    public void setCurScreenDirection(String curScreenDirection) {
        this.curScreenDirection = curScreenDirection;
    }
}
