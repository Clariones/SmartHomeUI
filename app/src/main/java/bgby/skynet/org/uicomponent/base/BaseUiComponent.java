package bgby.skynet.org.uicomponent.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import bgby.skynet.org.smarthomeui.device.IDevice;
import bgby.skynet.org.smarthomeui.layoutcomponent.ILayoutComponent;
import bgby.skynet.org.smarthomeui.uicontroller.UIControllerManager;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
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
    protected TextView displayNameTextView;


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

    protected void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设备名称"); //设置对话框标题
        builder.setIcon(android.R.drawable.btn_star); //设置对话框标题前的图标
        final EditText edit = new EditText(getContext());
        edit.setText(layoutData.getDisplayName());
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Editable txt = edit.getText();
                String newName = edit.getText().toString();
                onDisplayNameDialogClosed(newName);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(true); //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create(); //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

    protected void onDisplayNameDialogClosed(String newName) {
        if (newName == null){
            Controllers.showError("设备名称为空", layoutData.getDeviceID());
            return;
        }
        if (newName.isEmpty()){
            Controllers.showError("无效操作", "设备名称不能为空");
            return;
        }
        Controllers.getControllerManager().updateDeviceName(layoutData.getDeviceID(), newName);
    }

    protected void attachDisplayNameView(TextView txtView){
        displayNameTextView = txtView;
        txtView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showNameDialog();
                return true;
            }
        });
    }

    protected String getScreenDirection(){
        int direction = getActivity().getRequestedOrientation();
        if (direction == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT || direction == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return "portrait";
        } else {
            return "landscape";
        }
    }

    @Override
    public void onDeviceStatusChanged(IDevice device) {
        // by default, do nothing. Need each component handle this itself
    }

    @Override
    public void updateDisplayName(String displayName) {
        if (displayNameTextView == null){
            return;
        }
        displayNameTextView.setText(displayName);
    }

    protected void applyImageDrable(ImageView imgView, String meaterialName) {
        Controllers.getMaterialsManager().applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, imgView, meaterialName, null);
    }

    protected void applyBackground(View view, String meaterialName) {
        Controllers.getMaterialsManager().applyMaterial(MaterialsManager.APPLY_TO_BACKGROUND, view, meaterialName, null);
    }

    protected void applyFont(TextView textView, String meaterialName) {
        Controllers.getMaterialsManager().applyMaterial(MaterialsManager.APPLY_TO_FONT, textView, meaterialName, null);
    }
}
