package bgby.skynet.org.uicomponent.simpledimmer;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import bgby.skynet.org.smarthomeui.R;
import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.device.ISimpleDimmerDevice;
import bgby.skynet.org.smarthomeui.uimaterials.DrawableMaterail;
import bgby.skynet.org.smarthomeui.uimaterials.IMaterial;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.uicomponent.base.BaseUiComponent;

/**
 * Created by Clariones on 6/14/2016.
 */
public class SimpleDimmerFragment extends BaseUiComponent {
    protected static final String MATERIAL_BACKGROUD = "simpleDimmer/summary/1x1/background/";
    protected static final String MATERIAL_ON_ICON = "simpleDimmer/summary/1x1/state/on";
    protected static final String MATERIAL_OFF_ICON = "simpleDimmer/summary/1x1/state/off";
    protected static final String MATERIAL_MIDDLE_ICON = "simpleDimmer/summary/1x1/state/middle";
    protected static final String MATERIAL_NAME = "simpleDimmer/summary/1x1/displayName";
    protected static final int STATE_OFF = 0;
    protected static final int STATE_ON = 1;
    protected static final int STATE_MIDDLE = 2;
    protected static final Drawable[] STATE_ICONS = new Drawable[3];
    private static final String TAG = "simpleDimmerFragment";
    private View viewBackground;
    private TextView txtName;
    private ImageView imgModeIcon;
    private SeekBar seekBarLevel;
    private AlertDialog levelDialog;

    public static SimpleDimmerFragment newInstance(String componetID) {
        SimpleDimmerFragment fragment = new SimpleDimmerFragment();
        fragment.handleComponentID(componetID);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("CREATE FRAGMENT", "Create " + this.getClass().getSimpleName());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_uicmpt_simple_dimmer, container, false);
        viewBackground = view.findViewById(R.id.cmpt_simpleDimmer_background);
        txtName = (TextView) view.findViewById(R.id.cmpt_simpleDimmer_name);
        imgModeIcon = (ImageView) view.findViewById(R.id.cmpt_simpleDimmer_mode_icon);
        seekBarLevel = new SeekBar(getContext());
        seekBarLevel.setMax(100);
        this.levelDialog = createDialog();

        applyMaterials();
        getDimmerDevice().queryStatus();
        txtName.setText(layoutData.getDisplayName());
        updateValues();
        Log.d("CREATE FRAGMENT", "Finish Create " + this.getClass().getSimpleName());

        imgModeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLevelDialog();
            }
        });

        seekBarLevel.setOnSeekBarChangeListener(new SetLevelListener());
        attachDisplayNameView(txtName);
        return view;
    }


    private void updateValues() {
        ISimpleDimmerDevice device = getDimmerDevice();
        if (!device.getState()){
            imgModeIcon.setImageDrawable(STATE_ICONS[STATE_OFF]);
            return;
        }
        int level = device.getLevel();
        if (level <=0){
            imgModeIcon.setImageDrawable(STATE_ICONS[STATE_OFF]);
        }else if (level >= 99){
            imgModeIcon.setImageDrawable(STATE_ICONS[STATE_ON]);
        }else{
            imgModeIcon.setImageDrawable(STATE_ICONS[STATE_MIDDLE]);
        }
    }

    private ISimpleDimmerDevice getDimmerDevice() {
        return (ISimpleDimmerDevice) layoutData.getDevice();
    }

    private void applyMaterials() {
        MaterialsManager mmng = Controllers.getMaterialsManager();
        String materialBkg = MATERIAL_BACKGROUD + getScreenDirection();
        mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, viewBackground, materialBkg, null);
        mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, txtName, MATERIAL_NAME, null);

        if (STATE_ICONS[0] != null) {
            return;
        }
        IMaterial material = mmng.getMaterial(MATERIAL_OFF_ICON);
        if (material == null) {
            material = mmng.getMaterial(MaterialsManager.MATERIAL_ID_SWITCH_OFF);
        }
        if (material instanceof DrawableMaterail) {
            STATE_ICONS[STATE_OFF] = ((DrawableMaterail) material).getDrawable();
        }

        material = mmng.getMaterial(MATERIAL_ON_ICON);
        if (material == null) {
            material = mmng.getMaterial(MaterialsManager.MATERIAL_ID_SWITCH_ON);
        }
        if (material instanceof DrawableMaterail) {
            STATE_ICONS[STATE_ON] = ((DrawableMaterail) material).getDrawable();
        }
        material = mmng.getMaterial(MATERIAL_MIDDLE_ICON);
        if (material == null) {
            material = mmng.getMaterial(MaterialsManager.MATERIAL_ID_SWITCH_PARTIAL);
        }
        if (material instanceof DrawableMaterail) {
            STATE_ICONS[STATE_MIDDLE] = ((DrawableMaterail) material).getDrawable();
        }

    }

    @Override
    public void onDeviceStatusChanged(IDevice layoutComponent) {
        ISimpleDimmerDevice device = getDimmerDevice();
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

    private void changeLevel(int curLevel) {
        ISimpleDimmerDevice device = getDimmerDevice();
        device.setLevel(curLevel);
    }

    protected void showLevelDialog(){
        ISimpleDimmerDevice device = getDimmerDevice();
        seekBarLevel.setProgress(device.getLevel());

        levelDialog.show();
    }

    @NonNull
    private AlertDialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("亮度调整"); //设置对话框标题
//        builder.setIcon(android.R.drawable.btn_star); //设置对话框标题前的图标
        builder.setView(seekBarLevel);
        builder.setCancelable(false); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        return dialog;
    }

    private class SetLevelListener implements SeekBar.OnSeekBarChangeListener {
        protected int curLevel = 0;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            curLevel = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            levelDialog.cancel();
            changeLevel(curLevel);

        }
    }
}