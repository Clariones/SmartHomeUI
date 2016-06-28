package bgby.skynet.org.uicomponent.normalhvac;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.uicomponent.base.BaseUiComponent;

public class NormalHvacFragment extends BaseUiComponent {
    protected static final String MATERIAL_BACKGROUD = "normalHVAC/summary/1x1/background/";
    protected static final String MATERIAL_MODE_ICON = "normalHVAC/summary/1x1/runningMode/";
    protected static final String MATERIAL_TEMPERATURE = "normalHVAC/summary/1x1/roomTemperature";
    protected static final String MATERIAL_NAME = "normalHVAC/summary/1x1/displayName";



    protected View viewBackground;
    protected TextView txtTemperature;
    protected TextView txtName;
    protected ImageView imgModeIcon;
    protected String imgModeIconMaterial;

    protected String currentMode;
    protected String displayName;
    protected Double currentTemperature;

    protected static String defaultName = "OK";
    protected static String defaultTemperature = "90";
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
        updateValues();
        Log.d("CREATE FRAGMENT", "Finish Create " + this.getClass().getSimpleName());

        imgModeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openControlPage();
            }
        });
        return view;
    }

    private void openControlPage() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), NormalHvacControlActivity.class);
        intent.putExtra(ARG_COMPONENT_ID,layoutData.getComponentId());
        getActivity().startActivity(intent);
    }

    private void updateValues() {
        INormalHvacDevice device = (INormalHvacDevice) layoutData.getDevice();
        currentMode = device.getRunningMode();
        currentTemperature= device.getRoomTemperature();
        displayName = device.getDeviceDisplayName();

        updateDisplayMode();
        updateDisplayTemperature();
        updateDisplayName();
    }

    private void updateDisplayName() {
        if (displayName == null || displayName.isEmpty()){
            txtName.setText(R.string.common_undefined_device_name);
        }else{
            txtName.setText(displayName);
        }
    }

    private void updateDisplayTemperature() {
        if (currentTemperature == null){
            txtTemperature.setText(R.string.common_undefined_value);
            return;
        }
        String strTmpt = String.valueOf(currentTemperature).replaceAll("\\.?0*$","");
        txtTemperature.setText(strTmpt);
    }

    private void updateDisplayMode() {
        if (currentMode == null){
            imgModeIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.normal_hvac_summary));
            return;
        }
        String meaterialName = MATERIAL_MODE_ICON + currentMode;
        if (meaterialName.equals(imgModeIconMaterial)){
            return;
        }
        imgModeIconMaterial = meaterialName; // if mode not changed, then don't draw
        applyImageDrable(imgModeIcon, meaterialName);
    }

    protected void applyMaterials() {
        int direction = getActivity().getRequestedOrientation();
        String materialBkg = null;
        if (direction == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT || direction == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            materialBkg = MATERIAL_BACKGROUD+"portrait";
        }else{
            materialBkg = MATERIAL_BACKGROUD+"landscape";
        }
        applyImageDrable((ImageView) viewBackground, materialBkg);
        applyFont(txtTemperature, MATERIAL_TEMPERATURE);
        applyFont(txtName, MATERIAL_NAME);
    }

}
