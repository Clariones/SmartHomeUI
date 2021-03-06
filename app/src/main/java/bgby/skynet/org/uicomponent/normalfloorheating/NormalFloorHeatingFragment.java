package bgby.skynet.org.uicomponent.normalfloorheating;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.INormalFloorHeating;
import bgby.skynet.org.smarthomeui.uimaterials.DrawableMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.IMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.BaseUiComponent;

public class NormalFloorHeatingFragment extends BaseUiComponent {
    protected static final String MATERIAL_BACKGROUD = "floorheating/summary/1x1/background/";
    protected static final String MATERIAL_MODE_ICON = "floorheating/summary/1x1/state/";
    protected static final String MATERIAL_TEMPERATURE = "floorheating/summary/1x1/roomTemperature";
    protected static final String MATERIAL_NAME = "floorheating/summary/1x1/displayName";
    protected static final String TAG = "FloorHeatingFragment";
    protected static final int MODE_ON = 1;
    protected static final int MODE_OFF = 0;
    protected static final int MODE_UNKNOWN = 2;

    protected View viewBackground;
    protected TextView txtTemperature;
    protected TextView txtName;
    protected ImageView imgModeIcon;

    protected Drawable[] modeIcons = new Drawable[3];

    public NormalFloorHeatingFragment() {
        // Required empty public constructor
    }

    public static NormalFloorHeatingFragment newInstance(String componetID) {
        NormalFloorHeatingFragment fragment = new NormalFloorHeatingFragment();
        fragment.handleComponentID(componetID);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CREATE FRAGMENT", "Create " + this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.fragment_uicmpt_normal_floorheating, container, false);
        initMembers(view);
        applyMaterials();
        getMyDevice().queryStatus();
        updateValues();
        attachDisplayNameView(txtName);
        Log.d("CREATE FRAGMENT", "Finish Create " + this.getClass().getSimpleName());
        return view;
    }

    protected void updateValues() {
        INormalFloorHeating device = getMyDevice();
        Boolean state = device.getState();
        MaterialsManager mmng = Controllers.getMaterialsManager();
        if (state == null) {
            imgModeIcon.setImageDrawable(modeIcons[MODE_UNKNOWN]);
        } else {
            if (state) {
                imgModeIcon.setImageDrawable(modeIcons[MODE_ON]);
            } else {
                imgModeIcon.setImageDrawable(modeIcons[MODE_OFF]);
            }
        }

        Double dVal = device.getRoomTemperature();
        if (dVal != null) {
            String txt = String.valueOf(dVal).replaceAll("\\.?0*$", "");
            txtTemperature.setText(txt);
        }
    }

    protected void applyMaterials() {
        MaterialsManager mmng = Controllers.getMaterialsManager();
        String materialBkg = MATERIAL_BACKGROUD + getScreenDirection();
        mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, viewBackground, materialBkg, null);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, txtName, MATERIAL_NAME, null);
        txtName.setText(getDisplayName());
        mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, txtTemperature, MATERIAL_TEMPERATURE, null);
        // Note: mode icon is dynamic
        IMaterial material = mmng.getMaterial(MATERIAL_MODE_ICON + "on");
        if (material instanceof DrawableMaterial) {
            modeIcons[MODE_ON] = ((DrawableMaterial) material).getDrawable();
        } else {
            modeIcons[MODE_ON] = ((DrawableMaterial) mmng.getMaterial(MaterialsManager.MATERIAL_ID_FLOOR_HEATING_ON)).getDrawable();
        }
        material = mmng.getMaterial(MATERIAL_MODE_ICON + "off");
        if (material instanceof DrawableMaterial) {
            modeIcons[MODE_OFF] = ((DrawableMaterial) material).getDrawable();
        } else {
            modeIcons[MODE_OFF] = ((DrawableMaterial) mmng.getMaterial(MaterialsManager.MATERIAL_ID_FLOOR_HEATING_OFF)).getDrawable();
        }
        modeIcons[MODE_UNKNOWN] = ((DrawableMaterial) mmng.getMaterial(MaterialsManager.MATERIAL_ID_DEF_OTHER)).getDrawable();
    }

    protected void initMembers(View view) {
        this.viewBackground = view.findViewById(R.id.cmpt_normalfloorheat_background);
        this.txtName = (TextView) view.findViewById(R.id.cmpt_normalfloorheat_name);
        this.txtTemperature = (TextView) view.findViewById(R.id.cmpt_normalfloorheat_temperature);
        this.imgModeIcon = (ImageView) view.findViewById(R.id.cmpt_normalfloorheat_mode_icon);

        imgModeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openControlPage();
            }
        });
    }

    protected INormalFloorHeating getMyDevice() {
        return (INormalFloorHeating) layoutData.getDevice();
    }

    @Override
    public void onDeviceStatusChanged(IDevice device) {
        if (device != layoutData.getDevice()) {
            Log.w(TAG, "Got status update from other device " + device.getDeviceId());
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateValues();
            }
        });
    }

    protected void openControlPage() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), NormalFloorHeatingActivity.class);
        intent.putExtra(ARG_COMPONENT_ID, layoutData.getComponentId());
        getActivity().startActivity(intent);
    }
}
