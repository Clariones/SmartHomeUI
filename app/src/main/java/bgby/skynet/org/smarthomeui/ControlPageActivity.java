package bgby.skynet.org.smarthomeui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.skynet.bgby.driverutils.DriverUtils;

import java.util.List;

import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.smarthomeui.utils.Logger4Andriod;
import bgby.skynet.org.smarthomeui.layoutcomponent.ILayoutComponent;

public class ControlPageActivity extends FragmentActivity {
    public static final String MATERIAL_ID_LAND_BACKGROUND = "app/homepage/landscape/background";
    public static final String MATERIAL_ID_LAND_STATUS_BAR = "app/homepage/landscape/statusbar/background";
    public static final String MATERIAL_ID_LAND_LOGO = "app/homepage/landscape/statusbar/logo";
    public static final String MATERIAL_ID_LAND_NAME = "app/homepage/landscape/statusbar/name";
    public static final String MATERIAL_ID_LAND_MENU = "app/homepage/landscape/statusbar/menu";

    public static final String MATERIAL_ID_PORT_BACKGROUND = "app/homepage/portrait/background";
    public static final String MATERIAL_ID_PORT_STATUS_BAR = "app/homepage/portrait/statusbar/background";
    public static final String MATERIAL_ID_PORT_LOGO = "app/homepage/portrait/statusbar/logo";
    public static final String MATERIAL_ID_PORT_NAME = "app/homepage/portrait/statusbar/name";
    public static final String MATERIAL_ID_PORT_MENU = "app/homepage/portrait/statusbar/menu";
    private static final int REQUEST_CODE_BASIC_SETTING = 10001;

    protected ViewPager viewPager;
    protected ImageView imgLogoIcon;
    protected TextView txtPageName;
    protected ImageButton btnMenu;
    protected View layoutStatusBar;
    protected View wholeView;
    private int curPageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DriverUtils.setLogger(new Logger4Andriod());
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        Controllers.setScreenDirection(this);
        setContentView(R.layout.activity_control_page);


        wholeView = findViewById(R.id.cmptpage_whole_view);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        imgLogoIcon = (ImageView) findViewById(R.id.cmptpage_logoIcon);
        txtPageName = (TextView) findViewById(R.id.cmptpage_pageName);
        btnMenu = (ImageButton) findViewById(R.id.cmptpage_menuIcon);
        layoutStatusBar =  findViewById(R.id.cmptpage_statusBar);
//        layoutStatusBar.getBackground().setAlpha(128);
        applyMaterials();


        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingPage();
            }
        });
        ControlPagerAdapter mPagerAdapter = new ControlPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setPageName(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setPageName(0);

        txtPageName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showNameDialog();
                return true;
            }
        });
    }

    protected void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("页面名称"); //设置对话框标题
        builder.setIcon(android.R.drawable.btn_star); //设置对话框标题前的图标
        final EditText edit = new EditText(this);
        edit.setText(getCurrentPageName());
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
            Controllers.showError("无效的名字", "页面的名字不能为空");
            return;
        }
        if (newName.isEmpty()){
            Controllers.showError("无效的名字", "页面的名字不能为空");
            return;
        }
        Controllers.getControllerManager().saveDeviceName(Controllers.DISPLAY_NAME_PAGE +curPageNo, newName);
        txtPageName.setText(newName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Controllers.getControllerManager().stop();
    }

    private String getCurrentPageName() {
        String id = Controllers.DISPLAY_NAME_PAGE + curPageNo;
        String pageName = Controllers.getControllerManager().getDisplayName(id);
        if (pageName == null){
            pageName = "第"+(curPageNo+1)+"页";
        }
        return pageName;
    }

    private void openSettingPage() {
        Intent intent = new Intent();
        intent.setClassName(this, SettingsActivity.class.getName());
        startActivityForResult(intent, REQUEST_CODE_BASIC_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_BASIC_SETTING:
                afterSettingChanged();
                break;
            default:
                break;
        }
    }

    private void afterSettingChanged() {
        Controllers.setScreenDirection(this);
        Controllers.showError("配置改变", "你应该重启本应用");
    }

    private void setPageName(int position) {
        this.curPageNo = position;

        List<ILayoutComponent> pages = Controllers.getControllerManager().getLayoutComponentManager().getRootComponents();
        if (pages == null || pages.size() <= position){
            txtPageName.setText("");
            return;
        }
        String pageName = getCurrentPageName();
        txtPageName.setText(pageName);

    }

    private void applyMaterials(){
        int direction = getRequestedOrientation();
        MaterialsManager mmng = Controllers.getMaterialsManager();
        if (direction == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || direction == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
            mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, imgLogoIcon, MATERIAL_ID_PORT_LOGO, MaterialsManager.MATERIAL_ID_LOGO);
            mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, txtPageName, MATERIAL_ID_PORT_NAME, null);
            mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, btnMenu, MATERIAL_ID_PORT_MENU, null);
            mmng.applyMaterial(MaterialsManager.APPLY_TO_BACKGROUND, layoutStatusBar, MATERIAL_ID_PORT_STATUS_BAR, null);
            mmng.applyMaterial(MaterialsManager.APPLY_TO_BACKGROUND, wholeView, MATERIAL_ID_PORT_BACKGROUND, null);
        }else{
            mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, imgLogoIcon, MATERIAL_ID_LAND_LOGO, MaterialsManager.MATERIAL_ID_LOGO);
            mmng.applyMaterial(MaterialsManager.APPLY_TO_FONT, txtPageName, MATERIAL_ID_LAND_NAME, null);
            mmng.applyMaterial(MaterialsManager.APPLY_TO_DRAWABLE_IMAGE, btnMenu, MATERIAL_ID_LAND_MENU, null);
            mmng.applyMaterial(MaterialsManager.APPLY_TO_BACKGROUND, layoutStatusBar, MATERIAL_ID_LAND_STATUS_BAR, null);
            mmng.applyMaterial(MaterialsManager.APPLY_TO_BACKGROUND, wholeView, MATERIAL_ID_LAND_BACKGROUND, null);
        }
    }

}
