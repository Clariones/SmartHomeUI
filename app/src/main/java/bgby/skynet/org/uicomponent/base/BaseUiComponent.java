package bgby.skynet.org.uicomponent.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.uicontroller.UIControllerManager;
import bgby.skynet.org.smarthomeui.uimaterials.IMaterial;
import bgby.skynet.org.smarthomeui.utils.Controllers;

/**
 * Created by Clariones on 6/3/2016.
 */
public class BaseUiComponent extends Fragment implements  IUiComponent{
//    public static final String ARG_COMPONENT_RUNTIME_ID = "arg_component_runtime_id";
    public static final String ARG_DEVICE_ID = "arg_device_id";
    public static final String ARG_COMPONENT_ID = "arg_component_id";
    private static final String TAG = "BaseUiComponent";
    protected ILayoutComponent layoutData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String componetID = getArguments().getString(ARG_COMPONENT_ID);
        UIControllerManager uiController = Controllers.getControllerManager();
        ILayoutComponent layout = uiController.getLayoutComponent(componetID);
        setLayoutData(layout);

        IDevice device = layout.getDevice();
        if (device != null){
            uiController.registerDeviceRelatedUIComponent(device, this);
        }
    }

    @Override
    public void onDetach() {
        ILayoutComponent layout = getLayoutData();
        IDevice device = layout.getDevice();
        if (device != null){
            UIControllerManager uiController = Controllers.getControllerManager();
            uiController.unRegisterDeviceRelatedUIComponent(device, this);
        }
        super.onDetach();
    }

    @Override
    public void setLayoutData(ILayoutComponent layoutData) {
        this.layoutData = layoutData;
    }

    @Override
    public ILayoutComponent getLayoutData() {
        return layoutData;
    }

    protected void handleComponentID(String componetID) {
        Bundle args = new Bundle();
        args.putString(ARG_COMPONENT_ID, componetID);
        setArguments(args);
    }

    @Override
    public String getDisplayName(){
        return layoutData.getDisplayName();
    }

    @Override
    public void onDeviceStatusChanged(IDevice layoutComponent) {
        // by default, do nothing. Need each component handle this itself
    }

    protected void applyImageDrable(ImageView imgView, String meaterialName) {
        IMaterial drawable = Controllers.getMaterialsManager().getMaterial(meaterialName);
        if (drawable == null){
            Log.d(TAG, "apply image for " + meaterialName + " not found");
            return;
        }
        Log.d(TAG, "apply image for " + meaterialName + " not found");
        drawable.applyToDrawableImage(imgView);
    }

    protected void applyBackground(View view, String meaterialName) {
        IMaterial drawable = Controllers.getMaterialsManager().getMaterial(meaterialName);
        if (drawable == null){
            Log.d(TAG, "apply background for " + meaterialName + " not found");
            return;
        }
        drawable.applyToBackgroup(view);
        Log.d(TAG, "apply background for " + meaterialName);
    }

    protected void applyFont(TextView textView, String meaterialName) {

        IMaterial drawable = Controllers.getMaterialsManager().getMaterial(meaterialName);
        if (drawable == null){
            Log.d(TAG, "apply font for " + meaterialName + " not found");
            return;
        }
        Log.d(TAG, "apply font for " + meaterialName );
        drawable.applyToFont(textView);
    }
}
