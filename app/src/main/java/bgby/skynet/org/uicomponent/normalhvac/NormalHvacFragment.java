package bgby.skynet.org.uicomponent.normalhvac;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.INormalHvacDevice;
import bgby.skynet.org.smarthomeui.uimaterials.DrawableMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.IMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.BaseUiComponent;

public class NormalHvacFragment extends BaseUiComponent {
    protected static final String MATERIAL_BACKGROUD = "normalHVAC/summary/1x1/background/";
    protected static final String MATERIAL_MODE_ICON = "normalHVAC/summary/1x1/runningMode/";
    protected static final String MATERIAL_TEMPERATURE = "normalHVAC/summary/1x1/roomTemperature";
    protected static final String MATERIAL_NAME = "normalHVAC/summary/1x1/displayName";
    protected static final String TAG = "NormalHvacFragment";


    protected View viewBackground;
    protected TextView txtTemperature;
    protected TextView txtName;
    protected ImageView imgModeIcon;

    protected String currentMode;
    protected String displayName;
    protected Double currentTemperature;

    protected Map<String, Drawable> modeIcons;

    public NormalHvacFragment() {

    }

    public static NormalHvacFragment newInstance(String componetID) {
        NormalHvacFragment fragment = new NormalHvacFragment();
        fragment.handleComponentID(componetID);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CREATE FRAGMENT", "Create " + this.getClass().getSimpleName());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_uicmpt_normal_hvac, container, false);
        viewBackground = view.findViewById(R.id.cmpt_normalhvac_background);
        txtTemperature = (TextView) view.findViewById(R.id.cmpt_normalhvac_temperature);
        txtName = (TextView) view.findViewById(R.id.cmpt_normalhvac_name);
        imgModeIcon = (ImageView) view.findViewById(R.id.cmpt_normalhvac_mode_icon);
        
        applyMaterials();
        attachDisplayNameView(txtName);
        updateValues();
        Log.d("CREATE FRAGMENT", "Finish Create " + this.getClass().getSimpleName());

        imgModeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openControlPage();
            }
        });
        getHvacDevice().queryStatus();
        return view;
    }

    protected INormalHvacDevice getHvacDevice() {
        return (INormalHvacDevice) layoutData.getDevice();
    }

    protected void openControlPage() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), NormalHvacControlActivity.class);
        intent.putExtra(ARG_COMPONENT_ID,layoutData.getComponentId());
        getActivity().startActivity(intent);
    }

    protected void updateValues() {
        INormalHvacDevice device = getHvacDevice();
        currentMode = device.getRunningMode();
        currentTemperature= device.getRoomTemperature();
        displayName = device.getDisplayName();

        updateDisplayMode();
        updateDisplayTemperature();
        updateDisplayName();
    }

    protected void updateDisplayName() {
        if (displayName == null || displayName.isEmpty()){
            txtName.setText(R.string.common_undefined_device_name);
        }else{
            txtName.setText(displayName);
        }
    }

    protected void updateDisplayTemperature() {
        if (currentTemperature == null){
            txtTemperature.setText(R.string.common_undefined_value);
            return;
        }
        String strTmpt = String.valueOf(currentTemperature).replaceAll("\\.?0*$","");
        txtTemperature.setText(strTmpt);
    }

    protected void updateDisplayMode() {
        if (currentMode == null){
            imgModeIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.normal_hvac_summary));
            return;
        }
        String meaterialName = MATERIAL_MODE_ICON + currentMode;
        Drawable icon = modeIcons.get(meaterialName);
        imgModeIcon.setImageDrawable(icon);
//        if (meaterialName.equals(imgModeIconMaterial)){
//            return;
//        }
//        imgModeIconMaterial = meaterialName; // if mode not changed, then don't draw
//        applyImageDrable(imgModeIcon, meaterialName);
    }

    protected void applyMaterials() {
        String materialBkg = MATERIAL_BACKGROUD + getScreenDirection();
        applyImageDrable((ImageView) viewBackground, materialBkg);
        applyFont(txtTemperature, MATERIAL_TEMPERATURE);
        applyFont(txtName, MATERIAL_NAME);

        INormalHvacDevice device = getHvacDevice();
        String[] modes = device.getRunningModes();
        Map<String, Drawable> icons = new HashMap<>();
        MaterialsManager mmng = Controllers.getMaterialsManager();
        for(int i = 0; i< modes.length; i++){
            String mId = MATERIAL_MODE_ICON + modes[i];
            IMaterial material = mmng.getMaterial(mId);
            if (material instanceof DrawableMaterial){
                icons.put(mId, ((DrawableMaterial) material).getDrawable());
            }else{
                icons.put(mId, ((DrawableMaterial)mmng.getMaterial(NormalHvacControlActivity.defMaterialIds[i])).getDrawable());
            }
        }
        this.modeIcons = icons;
    }

    @Override
    public void onDeviceStatusChanged(IDevice device) {
        if (device != layoutData.getDevice()){
            Log.w(TAG, "Got status update from other device " + device.getDeviceId());
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateValues();
            }
        });
    }
}
