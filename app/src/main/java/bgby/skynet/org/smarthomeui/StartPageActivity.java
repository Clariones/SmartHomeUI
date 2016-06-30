package bgby.skynet.org.smarthomeui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.skynet.bgby.driverutils.DriverUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import bgby.skynet.org.smarthomeui.uicontroller.IInitialProgressCallback;
import bgby.skynet.org.smarthomeui.uicontroller.UIControllerConfig;
import bgby.skynet.org.smarthomeui.uicontroller.UIControllerManager;
import bgby.skynet.org.smarthomeui.uimaterials.MaterialsManager;
import bgby.skynet.org.smarthomeui.utils.Controllers;
import bgby.skynet.org.smarthomeui.utils.Logger4Andriod;

public class StartPageActivity extends Activity {

    public static final int REQUEST_CODE_BASIC_SETTING = 1001;
    public static final int REQUEST_CODE_START_RUN = 1002;

    private static final String TAG = "StartPageActivity";
    protected TextView txtTitle;
    protected TextView txtDecription;
    protected TextView txtErrorReport;
    protected Button btnErrorReport;
    protected ImageButton btnOpenSettings;
    protected ProgressBar progressBarStarting;
    protected View startingPhaseContentView;
    protected Handler progressHandler;
    private MaterialsManager materialManager;
    private StartingCallback callback;
    private UIControllerManager uiControlManager;
    private Thread startingThread;
    public boolean startSuccess = false;
    private WifiManager.WifiLock multicastLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DriverUtils.setLogger(new Logger4Andriod());
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Controllers.setScreenDirection(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_start_page);

        txtTitle = (TextView) findViewById(R.id.txtStartingTitle);
        txtDecription = (TextView) findViewById(R.id.txtStartingDecription);
        txtErrorReport = (TextView) findViewById(R.id.txtErrorReport);
        txtErrorReport.setMovementMethod(ScrollingMovementMethod.getInstance());
        btnErrorReport = (Button) findViewById(R.id.btnShowHideErrorReport);
        btnOpenSettings = (ImageButton) findViewById(R.id.btnOpenSettings);
        progressBarStarting = (ProgressBar) findViewById(R.id.progressBarStarting);
        startingPhaseContentView = findViewById(R.id.appContentView);

//        viewPager.setVisibility(View.GONE);
        this.callback = new StartingCallback();
        btnErrorReport.setOnClickListener(callback);
        btnOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettingPage();
            }
        });
        if (needBasicalSettings()) {
            gotoSettingPage();
            return;
        }

        // 打开 wifi组播
//        WifiManager wifi;
//        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        WifiManager.MulticastLock multicastLock = wifi.createMulticastLock("just some tag text");
//        multicastLock.acquire();
        this.startingThread = new Thread() {
            public void run() {
                startWorking();
            }
        };
        startingThread.start();
    }

    private void testFonts() {
        String[] fontFamilies = {
                "sans-serif",
                "arial",
                "helvetica",
                "tahoma",
                "verdana",
                "sans-serif-light",
                "sans-serif-thin",
                "sans-serif-condensed",
                "serif",
                "times",
                "times new roman",
                "palatino",
                "georgia",
                "baskerville",
                "goudy",
                "fantasy",
                "cursive",
                "ITC Stone Serif",
                "Droid Sans",
                "monospace",
                "courier",
                "courier new",
                "monaco",
        };
        LinearLayout ll = (LinearLayout) findViewById(R.id.llFonts);

        for (int i = 0; i < fontFamilies.length; i++) {
            String name = fontFamilies[i];
            TextView fontView = new TextView(this);
            fontView.setTypeface(Typeface.create(name, Typeface.NORMAL));
            fontView.setTextSize(24);
            fontView.setText("字体：" + name);
            ll.addView(fontView);
        }
    }

    @Override
    protected void onDestroy() {
        stopStarting();
        super.onDestroy();
    }

    private void startWorking() {
//        if (Controllers.getControllerManager() != null){
//            Log.w(TAG, "Already started");
//            return;
//        }

        callback.onProgress(0.01, "加载素材", "正在加载素材包...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        this.materialManager = new MaterialsManager();
        Controllers.setMaterialsManager(materialManager);
        materialManager.setContext(this.getBaseContext());
        String folderName = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsFragment.KEY_MATERIAL_FOLDER, "smarthome");
        File dir = new File(Environment.getExternalStorageDirectory(), folderName);
        File zipPackage = new File(dir, "uidata.zip");
        File propertyFile = new File(dir, "uidata.properties");
        try {
            materialManager.loadZipPackage(propertyFile, zipPackage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.onErrorReport("素材包" + zipPackage.getAbsolutePath() + "未找到。\n请联系工程人员确定素材包正确的放置在设备中。");
            callback.onStartingFinished(false);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            this.uiControlManager = new UIControllerManager();
            UIControllerConfig config = new UIControllerConfig();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            config.setControllerID(prefs.getString(SettingsFragment.KEY_MINE_ID, null));
            config.setDriverProxyAddress(InetAddress.getByName(prefs.getString(SettingsFragment.KEY_PROXY_ADDRESS, null)));
            config.setDriverProxyPort(Integer.parseInt(prefs.getString(SettingsFragment.KEY_PROXY_PORT, "-1")));
            config.setMulticastAddress(InetAddress.getByName(prefs.getString(SettingsFragment.KEY_MULTICAST_ADDRESS, null)));
            config.setMulticastPort(Integer.parseInt(prefs.getString(SettingsFragment.KEY_MULTICAST_PORT, "-1")));

            uiControlManager.setInternalFileFolder(getFilesDir());
            uiControlManager.init(config);
            uiControlManager.setProgressCallback(this.callback);
            Controllers.setControllerManager(uiControlManager);

            prepareBroadcast();

            WifiManager wifi;
            wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//            WifiManager.MulticastLock multicastLock = wifi.createMulticastLock("just some tag text");
             multicastLock = wifi.createWifiLock("mylock");
            multicastLock.acquire();

            uiControlManager.start();
        } catch (Exception e) {
            e.printStackTrace();
            callback.onErrorReport(DriverUtils.dumpExceptionToString(e));
            callback.onStartingFinished(false);
            return;
        }
    }

    private void prepareBroadcast() {

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();

        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        try {
            InetAddress addr = InetAddress.getByAddress(quads);
            Log.i(TAG, "broadcast address should be " + addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            String sAddr = addr.getHostAddress();
                            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                            boolean isIPv4 = sAddr.indexOf(':')<0;

//                            if (useIPv4) {
//                                if (isIPv4)
//                                    return sAddr;
//                            } else {
//                                if (!isIPv4) {
//                                    int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
//                                    return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
//                                }
//                            }toUpperCase
                            Log.i(TAG, "My IP Address: " + sAddr);
                        }
                    }
                }
            } catch (Exception ex) { } // for now eat exceptions
    }


    private void stopStarting() {
        Log.i(TAG, "STOP UIControlManager starting...");
        if (startSuccess){
            return;
        }

        if (multicastLock != null) {
            multicastLock.release();
        }
        if (uiControlManager != null) {
            uiControlManager.stopStartingThread();
        }

        if (startingThread != null) {
            startingThread.interrupt();
        }
    }

    private void gotoSettingPage() {
        stopStarting();
        DriverUtils.log(Level.WARNING, TAG, "Basical setting not set. Open preferneces page");
        Intent intent = new Intent();
        intent.setClassName(this, SettingsActivity.class.getName());
        startActivityForResult(intent, REQUEST_CODE_BASIC_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BASIC_SETTING) {
            if (needBasicalSettings()) {
                Toast.makeText(StartPageActivity.this, getResources().getString(R.string.promptTxtBasicSettings), Toast.LENGTH_SHORT).show();
                gotoSettingPage();
            } else {
                Controllers.setScreenDirection(this);
                startWorking();
            }
            return;
        }
    }

    private boolean needBasicalSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultID = getResources().getString(R.string.pref_mine_id_defval);
        String curID = prefs.getString(SettingsFragment.KEY_MINE_ID, defaultID);

        if (curID.equals(defaultID)) {
            return true;
        }
        return false;
    }

    protected void loadStartConfiguration() {
    }

    class StartingCallback implements IInitialProgressCallback, View.OnClickListener {

        @Override
        public void onProgress(final double progress, final String title, final String description) {
            StartPageActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarStarting.setProgress((int) (progress * 100));
                    txtDecription.setText(description);
                    txtTitle.setText(title);
                }
            });
        }

        @Override
        public void onErrorReport(final String s) {
            StartPageActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtErrorReport.setText(s);
                    btnErrorReport.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onStartingFinished(final boolean success) {
            StartPageActivity.this.runOnUiThread(new Runnable() {


                @Override
                public void run() {
                    if (success) {
                        Intent intent = new Intent();
                        intent.setClassName(StartPageActivity.this, ControlPageActivity.class.getName());
                        startSuccess = true;
                        startActivityForResult(intent, REQUEST_CODE_START_RUN);
                        finish();
                    } else {
                        txtTitle.setText("启动失败!");
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (txtErrorReport.getVisibility() == View.GONE) {
                txtErrorReport.setVisibility(View.VISIBLE);
            } else {
                txtErrorReport.setVisibility(View.GONE);
            }
        }
    }
}
