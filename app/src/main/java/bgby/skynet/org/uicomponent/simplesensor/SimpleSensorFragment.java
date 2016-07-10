package bgby.skynet.org.uicomponent.simplesensor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.skynet.bgby.devicestandard.SimpleSensor;

import java.util.HashMap;
import java.util.Map;

import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.ISimpleSensorDevice;
import bgby.skynet.org.smarthomeui.uimaterials.FontMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.BaseUiComponent;

/**
 * Created by Clariones on 6/14/2016.
 */
public class SimpleSensorFragment extends BaseUiComponent {
    protected static final String MATERIAL_BACKGROUD = "simpleSensor/summary/1x1/background/";
    protected static final String MATERIAL_GOOD_ICON = "simpleSensor/summary/1x1/state/good";
    protected static final String MATERIAL_NORMAL_ICON = "simpleSensor/summary/1x1/state/normal";
    protected static final String MATERIAL_BAD_ICON = "simpleSensor/summary/1x1/state/bad";
    protected static final String MATERIAL_GOOD_FONT = "simpleSensor/summary/1x1/statetext/good";
    protected static final String MATERIAL_NORMAL_FONT = "simpleSensor/summary/1x1/statetext/normal";
    protected static final String MATERIAL_BAD_FONT = "simpleSensor/summary/1x1/statetext/bad";
    protected static final String MATERIAL_NAME = "simpleSensor/summary/1x1/displayName";
    private View viewBackground;
    private TextView txtName;
    private TextView txtValue;
    private ImageView imgModeIcon;
    private static final String TAG = "simplesensorFragment";
    private Map<String, Drawable> measureLevelIcons;
    private Map<String, FontMaterial> measureLevelFonts;
    private Drawable unknowIcon;
    private Drawable lastIcon = null;
    private FontMaterial lastFont =  null;
    public static SimpleSensorFragment newInstance(String componetID) {
        SimpleSensorFragment fragment = new SimpleSensorFragment();
        fragment.handleComponentID(componetID);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CREATE FRAGMENT", "Create " + this.getClass().getSimpleName());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_uicmpt_simple_sensor, container, false);
        viewBackground = view.findViewById(R.id.cmpt_simplesensor_background);
        txtName = (TextView) view.findViewById(R.id.cmpt_simplesensor_name);
        txtValue = (TextView) view.findViewById(R.id.cmpt_simplesensor_value_text);
        imgModeIcon = (ImageView) view.findViewById(R.id.cmpt_simplesensor_mode_icon);
        applyMaterials();
        ISimpleSensorDevice device = getSensorDevice();
        device.queryStatus();
        txtName.setText(layoutData.getDisplayName());
        updateValues();
        Log.d("CREATE FRAGMENT", "Finish Create " + this.getClass().getSimpleName());

        imgModeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails();
            }
        });

        attachDisplayNameView(txtName);
        return view;
    }

    private void showDetails() {
        ISimpleSensorDevice device = getSensorDevice();
        String msg = device.getMeasureValue()+" " + device.getUnit();
        Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                setTitle(device.getMeasureName()).
                setMessage(msg).
                create();
        alertDialog.show();
    }


    private void updateValues() {
        ISimpleSensorDevice device = getSensorDevice();
       // TODO
        String level = device.getMeasureLevel();
        Drawable icon = measureLevelIcons.get(level);
        if (icon != null){
            if (lastIcon != icon) {
                imgModeIcon.setImageDrawable(icon);
                lastIcon = icon;
            }
        }else{
            imgModeIcon.setImageDrawable(unknowIcon);
        }
        FontMaterial font = measureLevelFonts.get(level);
        if (level != null && font != lastFont){
            font.applyToFont(txtValue);
            lastFont = font;
        }

        Double value = device.getMeasureValue();
        if (value != null){
            String txt = String.valueOf(value).replaceAll("\\.?0*$", "");
            txtValue.setText(txt);
        }
    }

    private ISimpleSensorDevice getSensorDevice() {
        return (ISimpleSensorDevice) layoutData.getDevice();
    }

    private void applyMaterials() {
        MaterialsManager mmng = Controllers.getMaterialsManager();
        String materialBkg = MATERIAL_BACKGROUD + getScreenDirection();
        mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, viewBackground, materialBkg, null);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, txtName, MATERIAL_NAME, null);
        unknowIcon = mmng.getDrawable(MaterialsManager.MATERIAL_ID_DEF_OTHER, null);

        measureLevelFonts = new HashMap<>();
        measureLevelFonts.put(SimpleSensor.TERM_MEASURE_LEVEL_GOOD, mmng.getFont(MATERIAL_GOOD_FONT, MaterialsManager.MATERIAL_ID_MEASURE_LEVEL_GOOD));
        measureLevelFonts.put(SimpleSensor.TERM_MEASURE_LEVEL_NORMAL, mmng.getFont(MATERIAL_NORMAL_FONT, MaterialsManager.MATERIAL_ID_MEASURE_LEVEL_NORMAL));
        measureLevelFonts.put(SimpleSensor.TERM_MEASURE_LEVEL_BAD, mmng.getFont(MATERIAL_BAD_FONT, MaterialsManager.MATERIAL_ID_MEASURE_LEVEL_BAD));

        measureLevelIcons = new HashMap<>();
        measureLevelIcons.put(SimpleSensor.TERM_MEASURE_LEVEL_GOOD, mmng.getDrawable(MATERIAL_GOOD_ICON, MaterialsManager.MATERIAL_ID_SENSOR_GOOD));
        measureLevelIcons.put(SimpleSensor.TERM_MEASURE_LEVEL_NORMAL, mmng.getDrawable(MATERIAL_NORMAL_ICON, MaterialsManager.MATERIAL_ID_SENSOR_NORMAL));
        measureLevelIcons.put(SimpleSensor.TERM_MEASURE_LEVEL_BAD, mmng.getDrawable(MATERIAL_BAD_ICON, MaterialsManager.MATERIAL_ID_SENSOR_BAD));
    }

    @Override
    public void onDeviceStatusChanged(IDevice layoutComponent) {
        ISimpleSensorDevice device = getSensorDevice();
        if (device != layoutComponent) {
            Log.w(TAG, "Why I got message from other device?");
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateValues();
            }
        });
    }

}