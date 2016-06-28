package bgby.skynet.org.smarthomeui;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.layout.ILayout;

import java.util.List;

import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.smarthomeui.utils.Logger4Andriod;
import bgby.skynet.org.uicomponent.base.ILayoutComponent;

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

    protected ViewPager viewPager;
    protected ImageView imgLogoIcon;
    protected TextView txtPageName;
    protected ImageButton btnMenu;
    protected View layoutStatusBar;
    protected View wholeView;
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


        // TODO just for test
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curDirection = getRequestedOrientation();
                switch (curDirection){
                    case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        saveDirection(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        saveDirection(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        break;
                    case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        saveDirection(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        break;
                    default:
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        saveDirection(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

            }
        });
        ControlPagerAdapter mPagerAdapter = new ControlPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("Scroll Page",String.format("p=%d,off=%f,pix=%d", position, positionOffset, positionOffsetPixels));
            }

            @Override
            public void onPageSelected(int position) {
                setPageName(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        setPageName(0);

    }

    private void saveDirection(int direction) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt(Controllers.PREFERENCE_KEY_DIRECTION, direction).apply();
    }

    private void setPageName(int position) {
        List<ILayout> pages = Controllers.getControllerManager().getLayoutPages();
        if (pages == null || pages.size() <= position){
            txtPageName.setText("");
        }else{
            ILayoutComponent compt = (ILayoutComponent) pages.get(position);
            txtPageName.setText(compt.getDisplayName());
        }
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
