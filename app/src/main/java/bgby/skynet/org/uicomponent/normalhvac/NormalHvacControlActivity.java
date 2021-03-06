package bgby.skynet.org.uicomponent.normalhvac;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.skynet.bgby.devicestandard.NormalHVAC;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import bgby.skynet.org.customviews.arcprogressbar.ArcProgressBar;
import bgby.skynet.org.customviews.roundseekbar.OnProgressChangedListener;
import bgby.skynet.org.customviews.roundseekbar.RoundSeekBar;
import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.INormalHvacDevice;
import bgby.skynet.org.smarthomeui.layoutcomponent.ILayoutComponent;
import bgby.skynet.org.smarthomeui.uicontroller.UIControllerManager;
import bgby.skynet.org.smarthomeui.uimaterials.ColorMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.DrawableMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.FontMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.IMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.IUiComponent;

public class NormalHvacControlActivity extends FragmentActivity implements IUiComponent{

    public static final String MATERIAL_ID_PREFIX = "normalHVAC/activity/%s";
    protected static final String TAG = "NormalHvacCtrl";
    protected static final String[] defMaterialIds = {
            MaterialsManager.MATERIAL_ID_DEF1, MaterialsManager.MATERIAL_ID_DEF2, MaterialsManager.MATERIAL_ID_DEF3,
            MaterialsManager.MATERIAL_ID_DEF4, MaterialsManager.MATERIAL_ID_DEF5, MaterialsManager.MATERIAL_ID_DEF6,
            MaterialsManager.MATERIAL_ID_DEF7, MaterialsManager.MATERIAL_ID_DEF8, MaterialsManager.MATERIAL_ID_DEF9
    };
    protected INormalHvacDevice device;
    protected View bkgView;
    protected View statuesBarBackground;
    protected ImageView imgLogo;
    protected TextView txtDisplayName;
    protected ImageButton btnReturn;
    protected ImageView imgTemperature;
    protected TextView txtRoomTemperature;
    protected TextView txtSetTemperature;
    protected ImageView imgHumidity;
    protected TextView txtRoomHumidity;
    protected ArcProgressBar barRoomTemperature;
    protected ArcProgressBar barSetTemperature;
    protected RoundSeekBar sliderSetTemperature;
    protected ImageView imgRunningMode;
    protected ImageView imgFanMode;
    protected Drawable[] runningModeDrawables;
    protected Drawable[] fanModeDrawables;
    private ILayoutComponent layoutComponet;

    @Override
    protected void onDestroy() {
//        Controllers.unRegisterUIComponent(device, this);
        Controllers.getControllerManager().unRegisterDeviceRelatedUIComponent(device, this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // step1: normal initial works for View
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        Controllers.setScreenDirection(this);
        setContentView(R.layout.cmptactivity_normal_hvac_control);

        // step2: bind activity to correct layoutComponent
        Bundle bundle = this.getIntent().getExtras();
        String layoutId = bundle.getString(NormalHvacFragment.ARG_COMPONENT_ID);
        UIControllerManager uiCtrl = Controllers.getControllerManager();
        ILayoutComponent layout = uiCtrl.getLayoutComponent(layoutId);
        setLayoutData(layout);

        // step3: connect this bkgView to correct device
        String deviceId = layout.getDeviceID();
        Log.i(TAG, "Create with device ID " + deviceId);
        device = (INormalHvacDevice) getDeviceById(deviceId);
        Controllers.getControllerManager().registerDeviceRelatedUIComponent(device, this);
        device.queryStatus();

        // step4: get handles of any needed UI element
        initMembers();
        applyMaterials();

        txtDisplayName.setText(getDisplayName());
        updateFanModeIcon();
        updateRunningModeIcon();
    }

    private IDevice getDeviceById(String deviceId) {
        UIControllerManager ctl = Controllers.getControllerManager();
        return ctl.getDeviceManager().getDevice(deviceId);
    }

    protected void onChangeSettingTemperature(float progress) {
        txtSetTemperature.setText(formatToString(progress));
        barSetTemperature.setProgress(progress);
        device.setTemperatureSetting((double) progress);
    }

    protected String formatToString(float progress) {
        return String.format(Locale.ENGLISH, "%3.1f", progress);
    }

    protected void initMembers() {
        bkgView = findViewById(R.id.normalHvac_activity_background);
        statuesBarBackground = findViewById(R.id.normalHvac_statusBar_background);
        imgLogo = (ImageView) findViewById(R.id.normalHvac_statusBar_logo);
        txtDisplayName = (TextView) findViewById(R.id.normalHvac_statusBar_displayName);
        btnReturn = (ImageButton) findViewById(R.id.normalHvac_statusBar_return);
        imgTemperature = (ImageView) findViewById(R.id.normalHvac_temperature_icon);
        txtRoomTemperature = (TextView) findViewById(R.id.normalHvac_temperature_room);
        txtSetTemperature = (TextView) findViewById(R.id.normalHvac_temperature_set);
        imgHumidity = (ImageView) findViewById(R.id.normalHvac_humidity_icon);
        txtRoomHumidity = (TextView) findViewById(R.id.normalHvac_humidity_room);
        barRoomTemperature = (ArcProgressBar) findViewById(R.id.normalHvac_temperature_room_bar);
        barSetTemperature = (ArcProgressBar) findViewById(R.id.normalHvac_temperature_set_bar);
        sliderSetTemperature = (RoundSeekBar) findViewById(R.id.normalHvac_temperature_set_slider);
        imgRunningMode = (ImageView) findViewById(R.id.normalHvac_runningmode_set);
        imgFanMode = (ImageView) findViewById(R.id.normalHvac_fanspeed_set);

        double minValue = Math.min(device.getRoomTemperatureLowLimit(), device.getSettingTemperatureLowLimit());
        double maxValue = Math.max(device.getRoomTemperatureHighLimit(), device.getSettingTemperatureHighLimit());
        barRoomTemperature.setMinValue((float) minValue);
        barRoomTemperature.setMaxValue((float) maxValue);
        barSetTemperature.setMinValue((float) minValue);
        barSetTemperature.setMaxValue((float) maxValue);
        sliderSetTemperature.setMinValue((float) minValue);
        sliderSetTemperature.setMaxValue((float) maxValue);
        sliderSetTemperature.setValueLowLimit(device.getSettingTemperatureLowLimit().floatValue());
        sliderSetTemperature.setValueHighLimit(device.getSettingTemperatureHighLimit().floatValue());
        sliderSetTemperature.setOnProgressChangedListener(new TemperatureSettingListener());

        if (device.getRoomTemperature() != null) {
            barRoomTemperature.setProgress(device.getRoomTemperature().floatValue());
            txtRoomTemperature.setText(formatToString(device.getRoomTemperature().floatValue()));
        }
        if (device.getTemperatureSetting() != null) {
            barSetTemperature.setProgress(device.getTemperatureSetting().floatValue());
            sliderSetTemperature.setProgress(device.getTemperatureSetting().floatValue());
            txtSetTemperature.setText(formatToString(device.getTemperatureSetting().floatValue()));
        }
        if (!device.isHasHumidity()) {
            findViewById(R.id.nu_humidity).setVisibility(View.INVISIBLE);
        }

        runningModeDrawables = getSelectionDrawables(device.getRunningModes(), NormalHVAC.TERM_RUNNING_MODE);
        imgRunningMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectionDialogFragment dlgFragment = new SelectionDialogFragment();
                dlgFragment.setTexts(device.getRunningModes());
                dlgFragment.setTitle("运行模式");
                dlgFragment.setIcons(runningModeDrawables);
                dlgFragment.setListener(new RunningModeSelectionListener());
                dlgFragment.show(getSupportFragmentManager(), "RunningModeSelection");
            }
        });

        fanModeDrawables = getSelectionDrawables(device.getFanModes(), NormalHVAC.TERM_FAN_MODE);
        imgFanMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectionDialogFragment dlgFragment = new SelectionDialogFragment();
                dlgFragment.setTexts(device.getFanModes());
                dlgFragment.setTitle("风扇模式");
                dlgFragment.setIcons(fanModeDrawables);
                dlgFragment.setListener(new FanModeSelectionListener());
                dlgFragment.show(getSupportFragmentManager(), "FanModeSelection");
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void applyMaterials() {
        String direction = getDirectionName();
        // background
        MaterialInfo.push(MaterialInfo.BACKGROUND, bkgView, direction, MATERIAL_ID_PREFIX + "/background", "app/homepage/" + direction + "/background");
        // status bar
        MaterialInfo.push(MaterialInfo.BACKGROUND, statuesBarBackground, direction, MATERIAL_ID_PREFIX + "/statusbar/background", null);
        MaterialInfo.push(MaterialInfo.IMAGE, imgLogo, direction, MATERIAL_ID_PREFIX + "/statusbar/logo", MaterialsManager.MATERIAL_ID_LOGO);
        MaterialInfo.push(MaterialInfo.FONT, txtDisplayName, direction, MATERIAL_ID_PREFIX + "/statusbar/name", null);
        MaterialInfo.push(MaterialInfo.IMAGE, btnReturn, direction, MATERIAL_ID_PREFIX + "/statusbar/close", null);
        // temperature
        MaterialInfo.push(MaterialInfo.IMAGE, imgTemperature, direction, MATERIAL_ID_PREFIX + "/temperature/icon", MaterialsManager.MATERIAL_ID_TEMPERATURE);
        MaterialInfo.push(MaterialInfo.FONT, txtRoomTemperature, direction, MATERIAL_ID_PREFIX + "/temperature/" + NormalHVAC.TERM_ROOM_TEMPERATURE, null);
        MaterialInfo.push(MaterialInfo.FONT, findViewById(R.id.normalHvac_temperature_room_unit), direction, MATERIAL_ID_PREFIX + "/temperature/" + NormalHVAC.TERM_ROOM_TEMPERATURE, null);
        MaterialInfo.push(MaterialInfo.FONT, txtSetTemperature, direction, MATERIAL_ID_PREFIX + "/temperature/" + NormalHVAC.TERM_SET_TEMPERATURE, null);
        MaterialInfo.push(MaterialInfo.FONT, findViewById(R.id.normalHvac_temperature_set_unit), direction, MATERIAL_ID_PREFIX + "/temperature/" + NormalHVAC.TERM_SET_TEMPERATURE, null);
        // humidity
        MaterialInfo.push(MaterialInfo.IMAGE, imgHumidity, direction, MATERIAL_ID_PREFIX + "/humidity/icon", MaterialsManager.MATERIAL_ID_TEMPERATURE);
        MaterialInfo.push(MaterialInfo.FONT, txtRoomHumidity, direction, MATERIAL_ID_PREFIX + "/humidity/" + NormalHVAC.TERM_ROOM_HUMIDITY, null);
        // progress bar - room temperature
        String tickMarkMaterial = String.format(MATERIAL_ID_PREFIX + "/roomTemperatureBar/tickMark", direction);
        IMaterial material = Controllers.getMaterialsManager().getMaterial(tickMarkMaterial);
        if (material instanceof FontMaterial) {
            FontMaterial font = (FontMaterial) material;
            if (font.getFontColor() != null) {
                barRoomTemperature.setTickMarkColor(font.getFontColor());
            }
            if (font.getFontSize() != null) {
                barRoomTemperature.setTickMarkTextSize(font.getFontSize());
            }
        }
        material = MaterialInfo.getMaterial("/roomTemperatureBar/low", direction);
        if (material instanceof ColorMaterial) {
            barRoomTemperature.setProgressBarLowColor(((ColorMaterial) material).getColor());
        }
        material = MaterialInfo.getMaterial("/roomTemperatureBar/middle", direction);
        if (material instanceof ColorMaterial) {
            barRoomTemperature.setProgressBarMiddleColor(((ColorMaterial) material).getColor());
        }
        material = MaterialInfo.getMaterial("/roomTemperatureBar/high", direction);
        if (material instanceof ColorMaterial) {
            barRoomTemperature.setProgressBarHighColor(((ColorMaterial) material).getColor());
        }
        material = MaterialInfo.getMaterial("/roomTemperatureBar/background", direction);
        if (material instanceof ColorMaterial) {
            barRoomTemperature.setProgressBarBackgroundColor(((ColorMaterial) material).getColor());
        }

        // progress bar - setting temperature
        material = MaterialInfo.getMaterial("/setTemperatureBar/low", direction);
        if (material instanceof ColorMaterial) {
            barSetTemperature.setProgressBarLowColor(((ColorMaterial) material).getColor());
        }
        material = MaterialInfo.getMaterial("/setTemperatureBar/middle", direction);
        if (material instanceof ColorMaterial) {
            barSetTemperature.setProgressBarMiddleColor(((ColorMaterial) material).getColor());
        }
        material = MaterialInfo.getMaterial("/setTemperatureBar/high", direction);
        if (material instanceof ColorMaterial) {
            barSetTemperature.setProgressBarHighColor(((ColorMaterial) material).getColor());
        }
        material = MaterialInfo.getMaterial("/setTemperatureBar/background", direction);
        if (material instanceof ColorMaterial) {
            barSetTemperature.setProgressBarBackgroundColor(((ColorMaterial) material).getColor());
        }
        // Temperature seek bar
        material = MaterialInfo.getMaterial("/setTemperatureDisk/background", direction);
        if (material instanceof DrawableMaterial) {
            sliderSetTemperature.setWheel(((DrawableMaterial) material).getDrawable());
        }
        material = MaterialInfo.getMaterial("/setTemperatureDisk/pointer", direction);
        if (material instanceof DrawableMaterial) {
            sliderSetTemperature.setArrow(((DrawableMaterial) material).getDrawable());
        }

        MaterialInfo.applyMaterials();
    }

    protected Drawable[] getSelectionDrawables(String[] selections, String materialKey) {
        String keyBase = getMaterialIdBase(materialKey);

        Drawable[] result = new Drawable[selections.length];
        MaterialsManager mmng = Controllers.getMaterialsManager();
        for (int i = 0; i < result.length; i++) {
            IMaterial drwMaterial = mmng.getMaterial(keyBase + selections[i]);
            Log.d(TAG, "Query material " + keyBase + selections[i] + " got " + drwMaterial);
            if (drwMaterial instanceof DrawableMaterial) {
                result[i] = ((DrawableMaterial) drwMaterial).getDrawable();
            } else {
                result[i] = ((DrawableMaterial) getSystemDefaultIcon(i + 1)).getDrawable();
            }
        }
        return result;
    }

    @NonNull
    protected String getMaterialIdBase(String materialKey) {
        int direction = getRequestedOrientation();
        String keyBase = "normalHVAC/activity/"; //runningMode";
        if (direction == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT || direction == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            keyBase = keyBase + "portrait/" + materialKey + "/";
        } else {
            keyBase = keyBase + "landscape/" + materialKey + "/";
        }
        return keyBase;
    }

    protected String getDirectionName() {
        int direction = getRequestedOrientation();
        if (direction == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT || direction == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return "portrait";
        } else {
            return "landscape";
        }
    }

    protected IMaterial getSystemDefaultIcon(int i) {
        if (i < 1 || i > 9) {
            return Controllers.getMaterialsManager().getMaterial(MaterialsManager.MATERIAL_ID_DEF_OTHER);
        }
        return Controllers.getMaterialsManager().getMaterial(defMaterialIds[i - 1]);
    }


    protected void onRunningModeChanged(String runningMode, int which) {
        device.setRunningMode(runningMode);
    }

    protected void updateRunningModeIcon() {
        String mode = device.getRunningMode();
        String[] validModes = device.getRunningModes();
        Drawable[] drawables = runningModeDrawables;
        ImageView view = imgRunningMode;
        applySelectedIcon(mode, validModes, drawables, view);
    }

    protected void onFanModeChanged(String fanMode, int which) {
        device.setFanMode(fanMode);
    }

    protected void updateFanModeIcon() {
        String mode = device.getFanMode();
        String[] validModes = device.getFanModes();
        Drawable[] drawables = fanModeDrawables;
        ImageView view = imgFanMode;
        applySelectedIcon(mode, validModes, drawables, view);
    }

    private void applySelectedIcon(String mode, String[] validModes, Drawable[] drawables, ImageView view) {
        if (mode == null) {
            IMaterial material = getSystemDefaultIcon(0);
            material.applyToDrawableImage(view);
            return;
        }

        for (int i = 0; i < validModes.length; i++) {
            if (mode.equals(validModes[i])) {
                view.setImageDrawable(drawables[i]);
                break;
            }
        }
    }

    @Override
    public void setLayoutData(ILayoutComponent layoutData) {
        this.layoutComponet = layoutData;
    }

    @Override
    public ILayoutComponent getLayoutData() {
        return layoutComponet;
    }

    @Override
    public String getDisplayName() {
        if (device == null){
            return null;
        }
        return device.getDisplayName();
    }

    @Override
    public void onDeviceStatusChanged(IDevice layoutComponent) {
        if (device != layoutComponent){
            Log.w(TAG, "Why I got an message from another device?");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateStatus();
            }
        });
    }

    @Override
    public void updateDisplayName(String displayName) {
        txtDisplayName.setText(displayName);
    }

    private void updateStatus() {
        updateFanModeIcon();
        updateRunningModeIcon();
        updateRoomTemperature();
        updateSetTemperature();
        updateHumidity();
    }

    private void updateHumidity() {
        Double val = device.getRoomHumidity();
        if (val == null){
            return;
        }
        this.txtRoomHumidity.setText(formatToString(val.floatValue()));
    }

    private void updateSetTemperature() {
        Double val = device.getTemperatureSetting();
        if (val == null){
            return;
        }
        this.txtSetTemperature.setText(formatToString(val.floatValue()));
        this.barSetTemperature.setProgress(val.floatValue());
        this.sliderSetTemperature.setProgress(val.floatValue());
    }

    private void updateRoomTemperature() {
        Double val = device.getRoomTemperature();
        if (val == null){
            return;
        }
        this.txtRoomTemperature.setText(formatToString(val.floatValue()));
        this.barRoomTemperature.setProgress(val.floatValue());
    }

    public static class MaterialInfo {
        public static final int IMAGE = 0;
        public static final int FONT = 1;
        public static final int BACKGROUND = 2;
        protected static List<MaterialInfo> tasks = new ArrayList<>();
        protected int action;
        protected View view;
        protected IMaterial material;

        public MaterialInfo(int action, View view, IMaterial material) {
            this.action = action;
            this.view = view;
            this.material = material;
        }

        public static void push(int action, View view, String direction, String materialIdFormat, String defMaterialId) {
            String mId = String.format(materialIdFormat, direction);
            IMaterial material = Controllers.getMaterialsManager().getMaterial(mId);
            if (material == null) {
                material = Controllers.getMaterialsManager().getMaterial(defMaterialId);
            }
            tasks.add(new MaterialInfo(action, view, material));
        }


        public static IMaterial getMaterial(String resName, String direction) {
            String mId = String.format(MATERIAL_ID_PREFIX + resName, direction);
            Log.i(TAG, "Try to apply material " + mId);
            return Controllers.getMaterialsManager().getMaterial(mId);
        }

        public static void applyMaterials() {
            Iterator<MaterialInfo> it = tasks.iterator();
            while (it.hasNext()) {
                MaterialInfo task = it.next();
                if (task.material == null) {
                    continue;
                }
                switch (task.action) {
                    case IMAGE:
                        task.material.applyToDrawableImage((ImageView) task.view);
                        break;
                    case FONT:
                        task.material.applyToFont((TextView) task.view);
                        break;
                    case BACKGROUND:
                        task.material.applyToBackgroup(task.view);
                        break;
                }
                it.remove();
            }
        }
    }

    protected class RunningModeSelectionListener implements SelectionDialogFragment.SelectionDialogListener {
        @Override
        public void onSelected(DialogInterface dialog, String selected, int which) {
            onRunningModeChanged(selected, which);
        }

        @Override
        public void onCancel(DialogInterface dialog, String selected, int which) {
        }
    }

    protected class FanModeSelectionListener implements SelectionDialogFragment.SelectionDialogListener {
        @Override
        public void onSelected(DialogInterface dialog, String selected, int which) {
            onFanModeChanged(selected, which);
        }

        @Override
        public void onCancel(DialogInterface dialog, String selected, int which) {
        }
    }

    private class TemperatureSettingListener implements OnProgressChangedListener {
        protected float curProgress;
        @Override
        public void onProgressChanged(RoundSeekBar roundSeekBar, float progress) {
            curProgress = progress;
            txtSetTemperature.setText(formatToString(progress));
            barSetTemperature.setProgress(progress);
        }

        @Override
        public void onStartTrackingTouch(RoundSeekBar roundSeekBar) {
        }

        @Override
        public void onStopTrackingTouch(RoundSeekBar roundSeekBar) {
            onChangeSettingTemperature(curProgress);
        }
    }
}
