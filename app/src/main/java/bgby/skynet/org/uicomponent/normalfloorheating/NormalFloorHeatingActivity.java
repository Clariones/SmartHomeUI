package bgby.skynet.org.uicomponent.normalfloorheating;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import bgby.skynet.org.customviews.arcprogressbar.ArcProgressBar;
import bgby.skynet.org.customviews.roundseekbar.OnProgressChangedListener;
import bgby.skynet.org.customviews.roundseekbar.RoundSeekBar;
import bgby.skynet.org.smarthomeui.ControlPageActivity;
import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.INormalFloorHeating;
import bgby.skynet.org.smarthomeui.uimaterials.ColorMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.DrawableMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.FontMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.IMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.BaseControlActivity;
import bgby.skynet.org.uicomponent.base.IUiComponent;

public class NormalFloorHeatingActivity extends BaseControlActivity implements IUiComponent {

    private static final String TAG = "FloorHeatingActivity";
    private static final String MATERIAL_PREFIX = "floorheating/activity/";
    private static final String MATERIAL_ACTIVITY_BACKGROUD = "/background";
    private static final String MATERIAL_STATUS_BACKGROUD = "/statusbar/background";
    private static final String MATERIAL_STATUS_LOGO = "/statusbar/logo";
    private static final String MATERIAL_STATUS_CLOSE = "/statusbar/close";
    private static final String MATERIAL_STATUS_DISPLAYNAME = "/statusbar/name";
    private static final String MATERIAL_TEMPT_ICON = "/temperature/icon";
    private static final String MATERIAL_TEMPT_SET = "/temperature/temperatureSetting";
    private static final String MATERIAL_TEMPT_ROOM = "/temperature/roomTemperature";
    private static final String MATERIAL_TEMPT_ROOMBAR_LOW = "/roomTemperatureBar/low";
    private static final String MATERIAL_TEMPT_ROOMBAR_MID = "/roomTemperatureBar/middle";
    private static final String MATERIAL_TEMPT_ROOMBAR_HIGH = "/roomTemperatureBar/high";
    private static final String MATERIAL_TEMPT_ROOMBAR_BKG = "/roomTemperatureBar/background";
    private static final String MATERIAL_TEMPT_ROOMBAR_MARK = "/roomTemperatureBar/tickMark";
    private static final String MATERIAL_TEMPT_SETBAR_LOW = "/setTemperatureBar/low";
    private static final String MATERIAL_TEMPT_SETBAR_MID = "/setTemperatureBar/middle";
    private static final String MATERIAL_TEMPT_SETBAR_HIGH = "/setTemperatureBar/high";
    private static final String MATERIAL_TEMPT_SETBAR_BKG = "/setTemperatureBar/background";
    private static final String MATERIAL_TEMPT_DISK_POINTER = "/setTemperatureDisk/pointer";
    private static final String MATERIAL_TEMPT_DISK_IMAGE = "/setTemperatureDisk/background";
    private static final String MATERIAL_STATE_ON = "/state/on";
    private static final String MATERIAL_STATE_OFF = "/state/off";
    protected View bkgView;
    protected View statuesBarBackground;
    protected ImageView imgLogo;
    protected TextView txtDisplayName;
    protected ImageButton btnReturn;
    protected ImageView imgTemperature;
    protected TextView txtRoomTemperature;
    protected TextView txtSetTemperature;
    protected ArcProgressBar barRoomTemperature;
    protected ArcProgressBar barSetTemperature;
    protected RoundSeekBar sliderSetTemperature;
    protected ImageButton btnState;
    protected Drawable[] stateIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cmptactivity_normal_floorheating_control);

        initMembers();
        applyMaterials();
        INormalFloorHeating device = getMyDevice();
        device.queryStatus();
        txtDisplayName.setText(getDisplayName());
        updateValues();
    }

    @Override
    protected String getMaterialPrefix() {
        return MATERIAL_PREFIX;
    }

    private void applyMaterials() {
        MaterialsManager mmng = Controllers.getMaterialsManager();
        String direction = getScreenDirection();
        String defMaterialId = ControlPageActivity.MATERIAL_ID_PORT_BACKGROUND;
        if (direction.equals("landscape")){
            defMaterialId = ControlPageActivity.MATERIAL_ID_LAND_BACKGROUND;
        }
        mmng.applyMaterial(MaterialsManager.APPLY_TO_BACKGROUND, bkgView, getMaterialID(MATERIAL_ACTIVITY_BACKGROUD), defMaterialId);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_BACKGROUND, statuesBarBackground, getMaterialID(MATERIAL_STATUS_BACKGROUD), null);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, imgLogo, getMaterialID(MATERIAL_STATUS_LOGO), MaterialsManager.MATERIAL_ID_LOGO);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, txtDisplayName, getMaterialID(MATERIAL_STATUS_DISPLAYNAME), null);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, btnReturn, getMaterialID(MATERIAL_STATUS_CLOSE), null);

        mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, imgTemperature, getMaterialID(MATERIAL_TEMPT_ICON), MaterialsManager.MATERIAL_ID_TEMPERATURE);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, txtRoomTemperature, getMaterialID(MATERIAL_TEMPT_ROOM), null);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, txtSetTemperature, getMaterialID(MATERIAL_TEMPT_SET), null);
        // bar and slider
        IMaterial material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_ROOMBAR_LOW));
        if (material instanceof ColorMaterial){
            barRoomTemperature.setProgressBarLowColor(((ColorMaterial) material).getColor());
        }
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_ROOMBAR_MID));
        if (material instanceof ColorMaterial){
            barRoomTemperature.setProgressBarMiddleColor(((ColorMaterial) material).getColor());
        }
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_ROOMBAR_HIGH));
        if (material instanceof ColorMaterial){
            barRoomTemperature.setProgressBarHighColor(((ColorMaterial) material).getColor());
        }
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_ROOMBAR_BKG));
        if (material instanceof ColorMaterial){
            barRoomTemperature.setProgressBarBackgroundColor(((ColorMaterial) material).getColor());
        }
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_ROOMBAR_MARK));
        if (material instanceof FontMaterial){
            FontMaterial font = (FontMaterial) material;
            if (font.getFontColor() != null) {
                barRoomTemperature.setTickMarkColor(font.getFontColor());
            }
            if (font.getFontSize() != null) {
                barRoomTemperature.setTickMarkTextSize(font.getFontSize());
            }
        }
        //
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_SETBAR_LOW));
        if (material instanceof ColorMaterial){
            barSetTemperature.setProgressBarLowColor(((ColorMaterial) material).getColor());
        }
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_SETBAR_MID));
        if (material instanceof ColorMaterial){
            barSetTemperature.setProgressBarMiddleColor(((ColorMaterial) material).getColor());
        }
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_SETBAR_HIGH));
        if (material instanceof ColorMaterial){
            barSetTemperature.setProgressBarHighColor(((ColorMaterial) material).getColor());
        }
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_SETBAR_BKG));
        if (material instanceof ColorMaterial){
            barSetTemperature.setProgressBarBackgroundColor(((ColorMaterial) material).getColor());
        }
        //
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_DISK_IMAGE));
        if (material instanceof DrawableMaterial){
            sliderSetTemperature.setWheel(((DrawableMaterial) material).getDrawable());
        }
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_DISK_POINTER));
        if (material instanceof DrawableMaterial){
            sliderSetTemperature.setArrow(((DrawableMaterial) material).getDrawable());
        }
        stateIcons = new Drawable[3];
        stateIcons[NormalFloorHeatingFragment.MODE_UNKNOWN] = mmng.getDrawable(MaterialsManager.MATERIAL_ID_DEF_OTHER, null);
        stateIcons[NormalFloorHeatingFragment.MODE_ON] = mmng.getDrawable(getMaterialID(MATERIAL_STATE_ON), MaterialsManager.MATERIAL_ID_CONTROL_ON);
        stateIcons[NormalFloorHeatingFragment.MODE_OFF] = mmng.getDrawable(getMaterialID(MATERIAL_STATE_OFF), MaterialsManager.MATERIAL_ID_CONTROL_OFF);

        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_ROOM));
        if (material instanceof FontMaterial){
            material.applyToFont(txtRoomTemperature);
            material.applyToFont((TextView) findViewById(R.id.normalFloorHeating_temperature_room_unit));
        }
        material = mmng.getMaterial(getMaterialID(MATERIAL_TEMPT_SET));
        if (material instanceof FontMaterial){
            material.applyToFont(txtSetTemperature);
            material.applyToFont((TextView) findViewById(R.id.normalFloorHeating_temperature_set_unit));
        }
    }

    private void initMembers() {
        bkgView = findViewById(R.id.normalFloorHeating_activity_background);
        statuesBarBackground = findViewById(R.id.normalFloorHeating_statusBar_background);
        imgLogo = (ImageView) findViewById(R.id.normalFloorHeating_statusBar_logo);
        txtDisplayName = (TextView) findViewById(R.id.normalFloorHeating_statusBar_displayName);
        btnReturn = (ImageButton) findViewById(R.id.normalFloorHeating_statusBar_return);

        imgTemperature = (ImageView) findViewById(R.id.normalFloorHeating_temperature_icon);
        txtRoomTemperature = (TextView) findViewById(R.id.normalFloorHeating_temperature_room);
        txtSetTemperature = (TextView) findViewById(R.id.normalFloorHeating_temperature_set);
        barRoomTemperature = (ArcProgressBar) findViewById(R.id.normalFloorHeating_temperature_room_bar);
        barSetTemperature = (ArcProgressBar) findViewById(R.id.normalFloorHeating_temperature_set_bar);
        sliderSetTemperature = (RoundSeekBar) findViewById(R.id.normalFloorHeating_temperature_set_slider);

        btnState = (ImageButton) findViewById(R.id.normalFloorHeating_state_button);

        INormalFloorHeating device = getMyDevice();
        float minValue = (float) Math.min(device.getRoomTemperatureLowLimit(),device.getTemperatureSettingLowLimit());
        float maxValue = (float) Math.max(device.getRoomTemperatureHighLimit(), device.getTemperatureSettingHighLimit());
        barRoomTemperature.setMinValue(minValue);
        barRoomTemperature.setMaxValue(maxValue);
        barSetTemperature.setMaxValue(maxValue);
        barSetTemperature.setMinValue(minValue);
        sliderSetTemperature.setMinValue(minValue);
        sliderSetTemperature.setMaxValue(maxValue);
        sliderSetTemperature.setValueHighLimit(device.getTemperatureSettingHighLimit().floatValue());
        sliderSetTemperature.setValueLowLimit(device.getTemperatureSettingLowLimit().floatValue());
        sliderSetTemperature.setOnProgressChangedListener(new OnProgressChangedListener() {
            protected float curProgress;
            @Override
            public void onProgressChanged(RoundSeekBar roundSeekBar, float progress) {
                curProgress = progress;
                txtSetTemperature.setText(formatTemperature(curProgress));
                barSetTemperature.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(RoundSeekBar roundSeekBar) {
            }

            @Override
            public void onStopTrackingTouch(RoundSeekBar roundSeekBar) {
                curProgress = roundSeekBar.getProgress();
                txtSetTemperature.setText(formatTemperature(curProgress));
                barSetTemperature.setProgress(curProgress);
                getMyDevice().setTemperatureSetting((double) curProgress);
            }
        });

        btnState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                INormalFloorHeating fhDev = getMyDevice();
                if (fhDev.getState() == null){
                    fhDev.setState(true);
                }else{
                    fhDev.setState(!fhDev.getState());
                }
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private INormalFloorHeating getMyDevice() {
        if (getLayoutData() == null) {
            return null;
        }
        if (getLayoutData().getDevice() instanceof INormalFloorHeating) {
            return (INormalFloorHeating) getLayoutData().getDevice();
        }
        return null;
    }

    @Override
    public void onDeviceStatusChanged(IDevice device) {
        if (device != getMyDevice()) {
            Log.w(TAG, "Why I got an message from another device?");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateValues();
            }
        });
    }

    private void updateValues() {
        INormalFloorHeating device = getMyDevice();
        Object value = device.getRoomTemperature();
        if (value instanceof Double){
            txtRoomTemperature.setText(formatTemperature(((Double) value).floatValue()));
            barRoomTemperature.setProgress(((Double) value).floatValue());
        }
        value = device.getTemperatureSetting();
        if (value instanceof Double){
            txtSetTemperature.setText(formatTemperature(((Double) value).floatValue()));
            barSetTemperature.setProgress(((Double) value).floatValue());
            sliderSetTemperature.setProgress(((Double) value).floatValue());
        }
        value = device.getState();
        if (value instanceof Boolean){
            if (((Boolean) value).booleanValue()){
                btnState.setImageDrawable(stateIcons[NormalFloorHeatingFragment.MODE_ON]);
            }else{
                btnState.setImageDrawable(stateIcons[NormalFloorHeatingFragment.MODE_OFF]);
            }
        }else{
            btnState.setImageDrawable(stateIcons[NormalFloorHeatingFragment.MODE_UNKNOWN]);
        }
    }

    @Override
    public void updateDisplayName(String displayName) {
        txtDisplayName.setText(displayName);
    }

}
